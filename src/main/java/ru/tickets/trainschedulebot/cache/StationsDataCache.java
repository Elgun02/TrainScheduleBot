package ru.tickets.trainschedulebot.cache;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author Elgun Dilanchiev
 */

@Getter
@Component
public class StationsDataCache implements StationCache{
    private final Map<String, Integer> stationCodeCache = new HashMap<>();

    @Override
    public Optional<String> getStationName(String stationNameParam) {
        return stationCodeCache.keySet().stream().filter(stationName -> stationName.equals(stationNameParam)).findFirst();
    }

    @Override
    public Optional<Integer> getStationCode(String stationNameParam) {
        return Optional.ofNullable(stationCodeCache.get(stationNameParam));
    }

    @Override
    public void addStationToCache(String stationName, int stationCode) {
        stationCodeCache.put(stationName,stationCode);
    }
}
