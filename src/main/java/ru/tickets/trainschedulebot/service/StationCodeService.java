package ru.tickets.trainschedulebot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.tickets.trainschedulebot.cache.StationsDataCache;
import ru.tickets.trainschedulebot.model.TrainStation;

import java.util.Optional;


@Service
@Slf4j
@RequiredArgsConstructor
public class StationCodeService {
    private final StationsDataCache stationsCache;
    private final StationBookService stationBookService;

    public int getStationCode(String stationName) {
        String stationNameParam = stationName.toUpperCase();

        Optional<Integer> stationCodeOptional = stationsCache.getStationCode(stationNameParam);
        if (stationCodeOptional.isPresent()) {
            return stationCodeOptional.get();
        } else if (processStationCodeRequest(stationNameParam).isEmpty()) {
            return -1;
        }

        return stationsCache.getStationCode(stationNameParam).orElse(-1);
    }

    private Optional<TrainStation[]> processStationCodeRequest(String stationNamePart) {
            TrainStation[] stations = stationBookService.getTrainStations(stationNamePart);

            if (stations == null || stations.length == 0) {
                return Optional.empty();
            }

            for (TrainStation station : stations) {
                stationsCache.addStationToCache(station.getStationName(), station.getStationCode());
            }

            return Optional.of(stations);
        }
    }