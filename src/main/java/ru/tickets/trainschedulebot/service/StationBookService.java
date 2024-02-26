package ru.tickets.trainschedulebot.service;

import lombok.RequiredArgsConstructor;
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

@Service
@RequiredArgsConstructor
public class StationBookService {
    private final RestTemplate restTemplate;
    private final StationsDataCache stationsCache;
    private final ReplyMessagesService messagesService;

    @Value("${station.code.service.request.template}")
    private String stationSearchTemplate;

    public SendMessage processStationNamePart(long chatId, String stationNamePartParam) {
        String searchedStationName = stationNamePartParam.toUpperCase();

        Optional<String> optionalStationName = stationsCache.getStationName(searchedStationName);
        if (optionalStationName.isPresent()) {
            return messagesService.getReplyMessage(chatId, "reply.stationBook.stationFound", Emojis.SUCCESS_MARK, optionalStationName.get());
        }

        List<TrainStation> trainStations = sendStationSearchRequest(searchedStationName);

        List<String> foundedStationNames = trainStations.stream().
                map(TrainStation::getStationName).filter(stationName -> stationName.contains(searchedStationName)).toList();

        if (foundedStationNames.isEmpty()) {
            return messagesService.getReplyMessage(chatId, "reply.stationBookMenu.stationNotFound");
        }

        StringBuilder stationsList = new StringBuilder();
        foundedStationNames.forEach(stationName -> stationsList.append(stationName).append("\n"));

        return messagesService.getReplyMessage(chatId, "reply.stationBook.stationsFound", Emojis.SUCCESS_MARK, stationsList.toString());

    }

    private List<TrainStation> sendStationSearchRequest(String stationNamePart) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<TrainStation[]> response =
                restTemplate.exchange(stationSearchTemplate,
                        HttpMethod.GET,
                        entity,
                        TrainStation[].class,
                        stationNamePart);
        TrainStation[] stations = response.getBody();
        if (stations == null) {
            return Collections.emptyList();
        }

        for (TrainStation station : stations) {
            stationsCache.addStationToCache(station.getStationName(), station.getStationCode());
        }

        return List.of(stations);
    }

}
