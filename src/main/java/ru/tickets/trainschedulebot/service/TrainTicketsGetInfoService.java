package ru.tickets.trainschedulebot.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ru.tickets.trainschedulebot.botApi.TelegramBot;
import ru.tickets.trainschedulebot.model.Train;

import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
@Getter
@Setter
public class TrainTicketsGetInfoService {
    @Value("${trainTicketsGetInfoService.ridRequestTemplate}")
    private String trainInfoRidRequestTemplate;
    @Value("${trainTicketsGetInfoService.trainInfoRequestTemplate}")
    private String trainInfoRequestTemplate;

    private static final String URI_PARAM_STATION_DEPART_CODE = "STATION_DEPART_CODE";
    private static final String URI_PARAM_STATION_ARRIVAL_CODE = "STATION_ARRIVAL_CODE";
    private static final String URI_PARAM_DATE_DEPART = "DATE_DEPART";
    private static final String TRAIN_DATE_IS_OUT_OF_DATE_MESSAGE = "находится за пределами периода";

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy");
    private final RestTemplate restTemplate;
    private final ReplyMessagesService messagesService;
    private final TelegramBot telegramBot;

    public TrainTicketsGetInfoService(RestTemplate restTemplate, ReplyMessagesService messagesService,
                                      TelegramBot telegramBot) {
        this.restTemplate = restTemplate;
        this.messagesService = messagesService;
        this.telegramBot = telegramBot;
    }

    public List<Train> getTrainTicketsList(long chatId, int stationDepartCode, int stationArrivalCode, Date dateDepart) {
        try {
            List<Train> trainList;
            String dateDepartStr = dateFormatter.format(dateDepart);
            Map<String, String> urlParams = new HashMap<>();
            urlParams.put(URI_PARAM_STATION_DEPART_CODE, String.valueOf(stationDepartCode));
            urlParams.put(URI_PARAM_STATION_ARRIVAL_CODE, String.valueOf(stationArrivalCode));
            urlParams.put(URI_PARAM_DATE_DEPART, dateDepartStr);

            Map<String, HttpHeaders> ridAndHttpHeaders = sendRidRequest(chatId, urlParams);
            if (ridAndHttpHeaders.isEmpty()) {
                return Collections.emptyList();
            }

            String ridValue = ridAndHttpHeaders.keySet().iterator().next();
            HttpHeaders httpHeaders = ridAndHttpHeaders.get(ridValue);

            List<String> cookies = httpHeaders.get(HttpHeaders.SET_COOKIE);
            if (cookies == null) {
                telegramBot.sendMessage(messagesService.getWarningReplyMessage(chatId, "reply.query.failed"));
                return Collections.emptyList();
            }

            HttpHeaders trainInfoRequestHeaders = new HttpHeaders();
            trainInfoRequestHeaders.put(HttpHeaders.COOKIE, cookies);
            trainInfoRequestHeaders.set(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3");

            String trainInfoResponseBody = sendTrainInfoJsonRequest(ridValue, trainInfoRequestHeaders);

            trainList = parseResponseBody(trainInfoResponseBody);
            return trainList;

        } catch (Exception e) {
            log.error("Ошибка при выполнении запроса: " + e.getMessage(), e);
            return Collections.emptyList();
        }
    }



    private Map<String, HttpHeaders> sendRidRequest(long chatId, Map<String, String> urlParams) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> passRzdResp = restTemplate.exchange(trainInfoRidRequestTemplate, HttpMethod.GET, entity, String.class, urlParams);
            String jsonRespBody = passRzdResp.getBody();

            if (isResponseBodyHasNoTrains(jsonRespBody)) {
                telegramBot.sendMessage(messagesService.getWarningReplyMessage(chatId, "reply.trainSearch.dateOutOfBoundError"));
                return Collections.emptyMap();
            }

            Optional<String> parsedRID = parseRID(jsonRespBody);
            return parsedRID.map(s -> Collections.singletonMap(s, passRzdResp.getHeaders())).orElse(Collections.emptyMap());

        } catch (HttpClientErrorException e) {
            log.error("Ошибка: " + e.getMessage());
            return Collections.emptyMap();

        } catch (RestClientException e) {
            log.error("Ошибка при выполнении запроса: " + e.getMessage());
            return Collections.emptyMap();
        }
    }

    private boolean isResponseResultRidDuplicate(ResponseEntity<String> resultResponse) {
        if (resultResponse.getBody() == null) {
            return true;
        }
        return resultResponse.getBody().contains("\"result\":\"RID");
    }

    private List<Train> parseResponseBody(String responseBody) {
        List<Train> trainList = null;
        try {
            JsonNode trainsNode = objectMapper.readTree(responseBody).path("tp").findPath("list");
            trainList = Arrays.asList(objectMapper.readValue(trainsNode.toString(), Train[].class));
        } catch (JsonProcessingException e) {
            e.getMessage();
        }

        return Objects.isNull(trainList) ? Collections.emptyList() : trainList;
    }

    private Optional<String> parseRID(String jsonRespBody) {
        String rid = null;
        try {
            JsonNode jsonNode = objectMapper.readTree(jsonRespBody.trim());
            JsonNode ridNode = jsonNode.get("RID");
            if (ridNode != null) {
                rid = ridNode.asText();
            }
        } catch (JsonProcessingException e) {
            e.getMessage();
        }

        return Optional.ofNullable(rid);
    }

    private String sendTrainInfoJsonRequest(String ridValue, HttpHeaders dataRequestHeaders) {
        HttpEntity<String> httpEntity = new HttpEntity<>(dataRequestHeaders);

        try {
            ResponseEntity<String> resultResponse = restTemplate.exchange(trainInfoRequestTemplate,
                    HttpMethod.GET,
                    httpEntity,
                    String.class, ridValue);

            while (isResponseResultRidDuplicate(resultResponse)) {
                resultResponse = restTemplate.exchange(trainInfoRequestTemplate,
                        HttpMethod.GET,
                        httpEntity,
                        String.class, ridValue);
            }

            String responseBody = resultResponse.getBody();
            return responseBody;

        } catch (HttpClientErrorException e) {
            log.error("Ошибка HTTP: " + e.getStatusCode() + ", " + e.getStatusText());
            log.error("Тело ответа: " + e.getResponseBodyAsString());
            throw e; // Можно обработать исключение здесь или прокинуть выше для обработки в другом месте

        } catch (RestClientException e) {
            log.error("Ошибка при выполнении запроса: " + e.getMessage());
            throw e; // То же самое здесь
        }
    }


    private boolean isResponseBodyHasNoTrains(String jsonRespBody) {
        return jsonRespBody == null || jsonRespBody.contains(TRAIN_DATE_IS_OUT_OF_DATE_MESSAGE);
    }

}
