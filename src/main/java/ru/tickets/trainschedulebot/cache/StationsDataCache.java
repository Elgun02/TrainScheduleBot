package ru.tickets.trainschedulebot.cache;

import lombok.Getter;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * The {@code StationsDataCache} class is an implementation of the {@link StationCache} interface
 * that serves as a caching mechanism for storing and retrieving information related to train stations.
 * It utilizes an in-memory {@link HashMap} to store station names and their corresponding codes.
 * <p>
 * This class is annotated with {@link Service} to indicate that it is a Spring bean and can be
 * automatically detected and instantiated by the Spring framework.
 *
 * @author Elgun Dilanchiev
 */
@Getter
@Service
public class StationsDataCache implements StationCache {

    /**
     * The in-memory cache to store station names and their corresponding codes.
     */
    private final Map<String, Integer> stationCodeCache = new HashMap<>();

    /**
     * Retrieves the station name based on the provided parameter from the in-memory cache.
     *
     * @param stationNameParam The parameter used to identify the station.
     * @return An {@link Optional} containing the station name, or empty if not found.
     */
    @Override
    public Optional<String> getStationName(String stationNameParam) {
        return stationCodeCache.keySet().stream().filter(stationName -> stationName.equals(stationNameParam)).findFirst();
    }

    /**
     * Retrieves the station code based on the provided parameter from the in-memory cache.
     *
     * @param stationNameParam The parameter used to identify the station.
     * @return An {@link Optional} containing the station code, or empty if not found.
     */
    @Override
    public Optional<Integer> getStationCode(String stationNameParam) {
        return Optional.ofNullable(stationCodeCache.get(stationNameParam));
    }

    /**
     * Adds a station to the in-memory cache with the provided name and code.
     *
     * @param stationName The name of the station to be added to the cache.
     * @param stationCode The code of the station to be added to the cache.
     */
    @Override
    public void addStationToCache(String stationName, int stationCode) {
        stationCodeCache.put(stationName, stationCode);
    }
}
