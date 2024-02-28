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

/**
 * Service class for retrieving information about train tickets from a remote API.
 * This class handles the process of obtaining a Request ID (RID) and sending requests
 * to fetch train ticket details based on the departure station, arrival station, and date.
 *
 * @author Elgun Dilanchiev
 * @version 1.0
 * @since 2024-02-28
 */
@Slf4j
@Service
@Getter
@Setter
@RequiredArgsConstructor
public class TrainTicketsGetInfoService {

    /**
     * Request template for obtaining RID (Request ID) from the remote API.
     */
    @Value("${trainTicketsGetInfoService.ridRequestTemplate}")
    private String trainInfoRidRequestTemplate;

    /**
     * Request template for obtaining train ticket details from the remote API.
     */
    @Value("${trainTicketsGetInfoService.trainInfoRequestTemplate}")
    private String trainInfoRequestTemplate;

    /**
     * Header name for HTTP requests.
     */
    @Value("${header.name}")
    private String headerName;

    /**
     * Header value for HTTP requests.
     */
    @Value("${header.value}")
    private String headerValue;

    /**
     * RestTemplate for making HTTP requests.
     */
    private final RestTemplate restTemplate;

    /**
     * Service for handling reply messages.
     */
    private final ReplyMessagesService messagesService;

    /**
     * Service for sending messages using Telegram.
     */
    private final SendMessageService sendMessageService;

    /**
     * ObjectMapper for JSON processing.
     */
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Date formatter for formatting dates.
     */
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy");

    /**
     * URI parameter key for the departure station code.
     */
    private static final String URI_PARAM_STATION_DEPART_CODE = "STATION_DEPART_CODE";

    /**
     * URI parameter key for the arrival station code.
     */
    private static final String URI_PARAM_STATION_ARRIVAL_CODE = "STATION_ARRIVAL_CODE";

    /**
     * URI parameter key for the departure date.
     */
    private static final String URI_PARAM_DATE_DEPART = "DATE_DEPART";

    /**
     * Message indicating that there are no trains available for the selected date.
     */
    private static final String TRAIN_DATE_IS_OUT_OF_DATE_MESSAGE = "Нету рейсов на выбранную дату.";

    /**
     * Retrieves a list of trains based on the provided criteria.
     *
     * @param chatId             The chat ID for sending messages.
     * @param stationDepartCode  The code of the departure station.
     * @param stationArrivalCode The code of the arrival station.
     * @param dateDepart         The departure date.
     * @return A list of trains matching the criteria.
     */
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

    /**
     * Constructs a map of URI parameters required for making a request to obtain train information.
     *
     * @param stationDepartCode The station code for the departure station.
     * @param stationArrivalCode The station code for the arrival station.
     * @param dateDepart The date of departure for the train.
     * @return A map of URI parameters containing station codes and the date of departure.
     */
    private HashMap<String, String> getUriParams(int stationDepartCode, int stationArrivalCode, Date dateDepart) {
        String dateDepartStr = dateFormatter.format(dateDepart);

        // Create a map to store URI parameters
        HashMap<String, String> urlParams = new HashMap<>();
        urlParams.put(URI_PARAM_STATION_DEPART_CODE, String.valueOf(stationDepartCode));
        urlParams.put(URI_PARAM_STATION_ARRIVAL_CODE, String.valueOf(stationArrivalCode));
        urlParams.put(URI_PARAM_DATE_DEPART, dateDepartStr);

        return urlParams;
    }

    /**
     * Checks if the response body contains a duplicate RID result, indicating a potential issue.
     *
     * @param resultResponse The response entity containing the HTTP result.
     * @return True if the response body indicates a duplicate RID result, otherwise false.
     */
    private boolean isResponseResultRidDuplicate(ResponseEntity<String> resultResponse) {
        if (resultResponse.getBody() == null) {
            return true;
        }
        return resultResponse.getBody().contains("\"result\":\"RID");
    }

    /**
     * Parses the JSON response body to extract information about available trains.
     *
     * @param responseBody The JSON response body received from the remote API.
     * @return A list of Train objects containing information about available trains.
     *         An empty list is returned if there is an issue parsing the response.
     */
    private List<Train> parseResponseBody(String responseBody) {
        try {
            // Retrieve the 'tp' node and its 'list' child node from the JSON response
            JsonNode trainsNode = objectMapper.readTree(responseBody).path("tp").findPath("list");
            if (trainsNode != null) {
                // Convert the 'list' node to an array of Train objects
                Train[] trainsArray = objectMapper.readValue(trainsNode.toString(), Train[].class);
                return Arrays.asList(trainsArray);
            } else {
                // Log a warning if the expected nodes are not found in the JSON response
                log.warn("Could not find 'tp' or 'list' in the JSON response: {}", responseBody);
                return Collections.emptyList();
            }
        } catch (JsonProcessingException e) {
            // Log an error if there is an issue parsing the JSON response
            log.error("Error occurred while parsing JSON response: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    /**
     * Parses the response body to retrieve the Request ID (RID) value.
     *
     * @param jsonRespBody The JSON response body from the RID request.
     * @return An Optional containing the RID value if present, otherwise empty.
     */
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

    /**
     * Sends a RID (Request ID) request to the remote API to obtain a unique identifier for subsequent requests.
     *
     * @param chatId   The chat ID for sending messages.
     * @param urlParams The URL parameters for the RID request.
     * @return A map containing the RID value and the HTTP headers from the RID request.
     */
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

    /**
     * Sends a JSON request to the remote API to obtain train ticket information.
     *
     * @param ridValue           The Request ID (RID) obtained from the RID request.
     * @param dataRequestHeaders The HTTP headers for the data request.
     * @return The JSON response body containing train ticket information.
     */
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

    /**
     * Checks if the response body indicates that there are no trains available for the selected date.
     *
     * @param jsonRespBody The JSON response body.
     * @return True if the response body indicates no trains are available, otherwise false.
     */
    private boolean isResponseBodyHasNoTrains(String jsonRespBody) {
        return jsonRespBody == null || jsonRespBody.contains(TRAIN_DATE_IS_OUT_OF_DATE_MESSAGE);
    }

    /**
     * Prepares the HTTP headers for the train information request.
     *
     * @param cookies The cookies obtained from the RID request.
     * @return The prepared HTTP headers for the train information request.
     */
    private HttpHeaders prepareTrainInfoRequestHeaders(List<String> cookies) {
        HttpHeaders trainInfoRequestHeaders = new HttpHeaders();
        trainInfoRequestHeaders.put(HttpHeaders.COOKIE, cookies);
        trainInfoRequestHeaders.set(HttpHeaders.USER_AGENT, headerValue);
        return trainInfoRequestHeaders;
    }
}