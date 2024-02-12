package ru.tickets.trainschedulebot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.tickets.trainschedulebot.cache.StationsDataCache;
import ru.tickets.trainschedulebot.model.TrainStation;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StationCodeService {
    @Value("${station.code.service.request.template}")
    private String stationCodeRequestTemplate;
    private RestTemplate restTemplate;
    private StationsDataCache stationsCache;


    public int getStationCode(String stationName) {
        String stationNameParam = stationName.toUpperCase();

        Optional<Integer> stationCodeOptional = stationsCache.getStationCode(stationNameParam);
        if (stationCodeOptional.isPresent()) return stationCodeOptional.get();

        if (processStationCodeRequest(stationNameParam).isEmpty()) {
            return -1;
        }

        return stationsCache.getStationCode(stationNameParam).orElse(-1);

    }

    private Optional<TrainStation[]> processStationCodeRequest(String stationNamePart) {
        ResponseEntity<TrainStation[]> response =
                restTemplate.getForEntity(
                        stationCodeRequestTemplate,
                        TrainStation[].class, stationNamePart);
        TrainStation[] stations = response.getBody();
        if (stations == null) {
            return Optional.empty();
        }

        for (TrainStation station : stations) {
            stationsCache.addStationToCache(station.getStationName(), station.getStationCode());
        }

        return Optional.of(stations);
    }
}
