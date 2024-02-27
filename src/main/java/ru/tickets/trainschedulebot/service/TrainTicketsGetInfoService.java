package ru.tickets.trainschedulebot.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ru.tickets.trainschedulebot.model.Train;

import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
@Getter
@Setter
@RequiredArgsConstructor
public class TrainTicketsGetInfoService {

    @Value("${trainTicketsGetInfoService.ridRequestTemplate}")
    private String trainInfoRidRequestTemplate;
    @Value("${trainTicketsGetInfoService.trainInfoRequestTemplate}")
    private String trainInfoRequestTemplate;
    @Value("${header.name}")
    private String headerName;
    @Value("${header.value}")
    private String headerValue;

    private final RestTemplate restTemplate;
    private final ReplyMessagesService messagesService;
    private final SendMessageService sendMessageService;

    private static final String URI_PARAM_STATION_DEPART_CODE = "STATION_DEPART_CODE";
    private static final String URI_PARAM_STATION_ARRIVAL_CODE = "STATION_ARRIVAL_CODE";
    private static final String URI_PARAM_DATE_DEPART = "DATE_DEPART";
    private static final String TRAIN_DATE_IS_OUT_OF_DATE_MESSAGE = "Нету рейсов на выбранную дату.";

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy");

    public List<Train> getTrainTicketsList(long chatId, int stationDepartCode, int stationArrivalCode, Date dateDepart) {
        try {
            List<Train> trainList;

            Map<String, HttpHeaders> ridAndHttpHeaders = sendRidRequest(chatId, getUriParams(stationDepartCode, stationArrivalCode, dateDepart));
            if (ridAndHttpHeaders.isEmpty()) {
                log.warn("No response received from sendRidRequest method");
                return Collections.emptyList();
            }

            String ridValue = ridAndHttpHeaders.keySet().iterator().next();
            HttpHeaders httpHeaders = ridAndHttpHeaders.get(ridValue);

            List<String> cookies = httpHeaders.get(HttpHeaders.SET_COOKIE);
            if (cookies == null) {
                log.warn("No cookies received from RID request");
                sendMessageService.sendMessage(messagesService.getWarningReplyMessage(chatId, "reply.query.failed"));
                return Collections.emptyList();
            }

            HttpHeaders trainInfoRequestHeaders = prepareTrainInfoRequestHeaders(cookies);
            String trainInfoResponseBody = sendTrainInfoJsonRequest(ridValue, trainInfoRequestHeaders);

            trainList = parseResponseBody(trainInfoResponseBody);
            return trainList;

        } catch (Exception e) {
            log.error("Error occurred while executing getTrainTicketsList method: " + e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    private HashMap<String, String> getUriParams(int stationDepartCode, int stationArrivalCode, Date dateDepart) {
        String dateDepartStr = dateFormatter.format(dateDepart);
        HashMap<String, String> urlParams = new HashMap<>();
        urlParams.put(URI_PARAM_STATION_DEPART_CODE, String.valueOf(stationDepartCode));
        urlParams.put(URI_PARAM_STATION_ARRIVAL_CODE, String.valueOf(stationArrivalCode));
        urlParams.put(URI_PARAM_DATE_DEPART, dateDepartStr);
        return urlParams;
    }

    private boolean isResponseResultRidDuplicate(ResponseEntity<String> resultResponse) {
        if (resultResponse.getBody() == null) {
            return true;
        }
        return resultResponse.getBody().contains("\"result\":\"RID");
    }

    private List<Train> parseResponseBody(String responseBody) {
        try {
            JsonNode trainsNode = objectMapper.readTree(responseBody).path("tp").findPath("list");
            if (trainsNode != null) {
                Train[] trainsArray = objectMapper.readValue(trainsNode.toString(), Train[].class);
                return Arrays.asList(trainsArray);
            } else {
                log.warn("Could not find 'tp' or 'list' in the JSON response: {}", responseBody);
                return Collections.emptyList();
            }
        } catch (JsonProcessingException e) {
            log.error("Error occurred while parsing JSON response: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    private Optional<String> parseRID(String jsonRespBody) {
        try {
            JsonNode jsonNode = objectMapper.readTree(jsonRespBody.trim());
            JsonNode ridNode = jsonNode.get("RID");
            return Optional.ofNullable(ridNode).map(JsonNode::asText);
        } catch (JsonProcessingException e) {
            log.error("Error occurred while parsing RID from JSON response: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    private Map<String, HttpHeaders> sendRidRequest(long chatId, Map<String, String> urlParams) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.USER_AGENT, headerValue);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> passRzdResp = restTemplate.exchange(
                    trainInfoRidRequestTemplate,
                    HttpMethod.GET,
                    entity,
                    String.class,
                    urlParams);

            String jsonRespBody = passRzdResp.getBody();

            if (isResponseBodyHasNoTrains(jsonRespBody)) {
                sendMessageService.sendMessage(messagesService.getWarningReplyMessage(chatId, "reply.trainSearch.dateOutOfBoundError"));
                return Collections.emptyMap();
            }

            Optional<String> parsedRID = parseRID(jsonRespBody);
            return parsedRID.map(s -> Collections.singletonMap(s, passRzdResp.getHeaders())).orElse(Collections.emptyMap());

        } catch (RestClientException e) {
            log.error("Error occurred while sending RID request: {}", e.getMessage(), e);
            return Collections.emptyMap();
        }
    }

    private String sendTrainInfoJsonRequest(String ridValue, HttpHeaders dataRequestHeaders) {
        HttpEntity<String> httpEntity = new HttpEntity<>(dataRequestHeaders);

        ResponseEntity<String> resultResponse = restTemplate.exchange(
                trainInfoRequestTemplate,
                HttpMethod.GET,
                httpEntity,
                String.class, ridValue);

        while (isResponseResultRidDuplicate(resultResponse)) {
            resultResponse = restTemplate.exchange(
                    trainInfoRequestTemplate,
                    HttpMethod.GET,
                    httpEntity,
                    String.class, ridValue);
        }

        return resultResponse.getBody();
    }

    private boolean isResponseBodyHasNoTrains(String jsonRespBody) {
        return jsonRespBody == null || jsonRespBody.contains(TRAIN_DATE_IS_OUT_OF_DATE_MESSAGE);
    }

    private HttpHeaders prepareTrainInfoRequestHeaders(List<String> cookies) {
        HttpHeaders trainInfoRequestHeaders = new HttpHeaders();
        trainInfoRequestHeaders.put(HttpHeaders.COOKIE, cookies);
        trainInfoRequestHeaders.set(HttpHeaders.USER_AGENT, headerValue);
        return trainInfoRequestHeaders;
    }
}