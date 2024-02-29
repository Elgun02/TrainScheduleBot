package ru.tickets.trainschedulebot.cache;

import java.util.Optional;

/**
 * The {@code StationCache} interface represents a caching mechanism for storing and retrieving
 * information related to train stations. It provides methods to get the station name and code
 * based on a provided parameter, as well as to add a station to the cache.
 * <p>
 * Implementations of this interface should handle the storage and retrieval of station information
 * in a manner suitable for the underlying storage mechanism, whether it be in-memory storage, a
 * database, or any other persistent storage.
 *
 * @author Elgun Dilanchiev
 * @version 1.0
 * @since 2024-02-29
 */
public interface StationCache {

    /**
     * Retrieves the station name based on the provided parameter.
     *
     * @param stationNameParam The parameter used to identify the station.
     * @return An {@link Optional} containing the station name, or empty if not found.
     */
    Optional<String> getStationName(String stationNameParam);

    /**
     * Retrieves the station code based on the provided parameter.
     *
     * @param stationNameParam The parameter used to identify the station.
     * @return An {@link Optional} containing the station code, or empty if not found.
     */
    Optional<Integer> getStationCode(String stationNameParam);

    /**
     * Adds a station to the cache with the provided name and code.
     *
     * @param stationName The name of the station to be added to the cache.
     * @param stationCode The code of the station to be added to the cache.
     */
    void addStationToCache(String stationName, int stationCode);
}
