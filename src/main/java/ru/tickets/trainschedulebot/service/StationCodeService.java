package ru.tickets.trainschedulebot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.tickets.trainschedulebot.cache.StationsDataCache;
import ru.tickets.trainschedulebot.model.TrainStation;

import java.util.Optional;

/**
 * Service class for retrieving station codes based on station names.
 * This class interacts with the station data cache and the StationBookService to obtain and cache station information.
 *
 * @author Elgun Dilanchiev
 * @version 1.0
 * @since 2024-02-28
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class StationCodeService {

    /**
     * The cache for storing train station data.
     */
    private final StationsDataCache stationsCache;

    /**
     * The service for processing train station information.
     */
    private final StationBookService stationBookService;

    /**
     * Retrieves the station code for the specified station name.
     *
     * @param stationName The name of the train station.
     * @return The station code if found; otherwise, -1.
     */
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

    /**
     * Processes a station code request and caches the results in the station data cache.
     *
     * @param stationNamePart The partial station name for which to search.
     * @return An Optional containing an array of TrainStation objects representing the search results if found; otherwise, empty.
     */
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