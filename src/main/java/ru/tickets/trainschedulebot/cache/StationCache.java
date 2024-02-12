package ru.tickets.trainschedulebot.cache;

import java.util.Optional;

/**
 * @author Elgun Dilanchiev
 */

public interface StationCache {
    Optional<String> getStationName(String stationNameParam);

    Optional<Integer> getStationCode(String stationNameParam);

    void addStationToCache(String stationName, int stationCode);
}
