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


@Slf4j
@Service
@RequiredArgsConstructor
public class StationBookService {
    private final RestTemplate restTemplate;
    private final StationsDataCache stationsCache;
    private final ReplyMessagesService messagesService;

    @Value("${station.code.service.request.template}")
    private String stationSearchTemplate;

    @Value("${header.name}")
    private String headerName;
    @Value("${header.value}")
    private String headerValue;

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


    private List<String> filterStationNames(List<TrainStation> trainStations, String searchedStationName) {
        return trainStations.stream()
                .map(TrainStation::getStationName)
                .filter(stationName -> stationName.contains(searchedStationName))
                .toList();
    }

    private StringBuilder buildStationsList(List<String> foundedStationNames) {
        StringBuilder stationsList = new StringBuilder();
        foundedStationNames.forEach(stationName -> stationsList.append(stationName).append("\n"));
        return stationsList;
    }

    private List<TrainStation> sendStationSearchRequest(String stationNamePart) {
        TrainStation[] stations = getTrainStations(stationNamePart);

        if (stations == null) {
            return Collections.emptyList();
        }

        cacheStations(stations);
        return List.of(stations);
    }

    private void cacheStations(TrainStation[] stations) {
        for (TrainStation station : stations) {
            stationsCache.addStationToCache(station.getStationName(), station.getStationCode());
        }
    }
}