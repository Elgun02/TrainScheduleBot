package ru.tickets.trainschedulebot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.tickets.trainschedulebot.cache.StationsDataCache;
import ru.tickets.trainschedulebot.model.TrainStation;
import ru.tickets.trainschedulebot.utils.Emojis;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Service class for processing train station information.
 * This class interacts with external APIs to search for train stations based on user input and provides station-related functionality.
 *
 * @author Elgun Dilanchiev
 * @version 1.0
 * @since 2024-02-28
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StationBookService {

    /**
     * The RestTemplate for making HTTP requests to external APIs.
     */
    private final RestTemplate restTemplate;

    /**
     * The cache for storing train station data.
     */
    private final StationsDataCache stationsCache;

    /**
     * The service for generating reply messages.
     */
    private final ReplyMessagesService messagesService;

    /**
     * The template for constructing the station search request URL.
     */
    @Value("${station.code.service.request.template}")
    private String stationSearchTemplate;

    /**
     * The name of the header used in HTTP requests.
     */
    @Value("${header.name}")
    private String headerName;

    /**
     * The value of the header used in HTTP requests.
     */
    @Value("${header.value}")
    private String headerValue;

    /**
     * Processes a partial station name and sends a reply message to the specified chat ID based on the search results.
     *
     * @param chatId               The ID of the chat to which the message will be sent.
     * @param stationNamePartParam The partial station name provided by the user.
     * @return A SendMessage object representing the reply message.
     */
    public SendMessage processStationNamePart(long chatId, String stationNamePartParam) {
        String searchedStationName = stationNamePartParam.toUpperCase();

        Optional<String> optionalStationName = stationsCache.getStationName(searchedStationName);
        if (optionalStationName.isPresent()) {
            return messagesService.getReplyMessage(chatId, "reply.stationBook.stationFound", Emojis.SUCCESS_MARK, optionalStationName.get());
        }

        List<TrainStation> trainStations = sendStationSearchRequest(searchedStationName);
        List<String> foundedStationNames = filterStationNames(trainStations, searchedStationName);

        if (foundedStationNames.isEmpty()) {
            return messagesService.getReplyMessage(chatId, "reply.stationBookMenu.stationNotFound");
        }
        StringBuilder stationsList = buildStationsList(foundedStationNames);

        return messagesService.getReplyMessage(chatId, "reply.stationBook.stationsFound", Emojis.SUCCESS_MARK, stationsList.toString());
    }

    /**
     * Retrieves an array of train stations based on the provided partial station name.
     *
     * @param stationNamePart The partial station name for which to search.
     * @return An array of TrainStation objects representing the search results.
     */
    public TrainStation[] getTrainStations(String stationNamePart) {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);
        headers.set(headerName, headerValue);

        try {
            ResponseEntity<TrainStation[]> response = restTemplate.exchange(
                    stationSearchTemplate,
                    HttpMethod.GET,
                    entity,
                    TrainStation[].class,
                    stationNamePart
            );

            return response.getBody();
        } catch (Exception e) {
            log.error("Error occurred while fetching train stations: {}", e.getMessage());
            return new TrainStation[0];
        }
    }

    /**
     * Filters station names based on the provided list of train stations and the searched station name.
     *
     * @param trainStations         The list of train stations to filter.
     * @param searchedStationName   The searched station name.
     * @return A list of filtered station names.
     */
    private List<String> filterStationNames(List<TrainStation> trainStations, String searchedStationName) {
        return trainStations.stream()
                .map(TrainStation::getStationName)
                .filter(stationName -> stationName.contains(searchedStationName))
                .toList();
    }

    /**
     * Builds a StringBuilder containing a list of station names.
     *
     * @param foundedStationNames The list of founded station names.
     * @return A StringBuilder containing the list of station names.
     */
    private StringBuilder buildStationsList(List<String> foundedStationNames) {
        StringBuilder stationsList = new StringBuilder();
        foundedStationNames.forEach(stationName -> stationsList.append(stationName).append("\n"));
        return stationsList;
    }

    /**
     * Sends a station search request and caches the results.
     *
     * @param stationNamePart The partial station name for which to search.
     * @return A list of TrainStation objects representing the search results.
     */
    private List<TrainStation> sendStationSearchRequest(String stationNamePart) {
        TrainStation[] stations = getTrainStations(stationNamePart);

        if (stations == null) {
            return Collections.emptyList();
        }

        cacheStations(stations);
        return List.of(stations);
    }

    /**
     * Caches the train stations in the station data cache.
     *
     * @param stations The array of TrainStation objects to cache.
     */
    private void cacheStations(TrainStation[] stations) {
        for (TrainStation station : stations) {
            stationsCache.addStationToCache(station.getStationName(), station.getStationCode());
        }
    }
}