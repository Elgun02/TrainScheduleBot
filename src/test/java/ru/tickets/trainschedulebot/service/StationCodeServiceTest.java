package ru.tickets.trainschedulebot.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.tickets.trainschedulebot.cache.StationsDataCache;
import ru.tickets.trainschedulebot.model.TrainStation;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StationCodeServiceTest {

    @Mock
    private StationsDataCache stationsCache;

    @Mock
    private StationBookService stationBookService;

    @InjectMocks
    private StationCodeService stationCodeService;

    private long chatId;
    private String stationNamePart;

    @BeforeEach
    void setUp() {
        chatId = 123L;
        stationNamePart = "TestStationMoscow".toUpperCase();
    }

    @Test
    void testGetStationCode_StationCodeInCache_ReturnsInt() {
        int stationCodeOptional = 200400;
        when(stationsCache.getStationCode(stationNamePart)).thenReturn(Optional.of(stationCodeOptional));
        int result = stationCodeService.getStationCode(stationNamePart);
        assertEquals(stationCodeOptional, result);
        verify(stationsCache, times(1)).getStationCode(stationNamePart);
    }

    @Test
    void getStationCode_CacheDoesNotContainCode_ProcessRequestNotEmpty_ShouldReturnCode() {
        when(stationsCache.getStationCode(stationNamePart)).thenReturn(Optional.empty());
        when(stationBookService.getTrainStations(stationNamePart)).thenReturn(new TrainStation[]{new TrainStation(stationNamePart, 456)});
        int result = stationCodeService.getStationCode(stationNamePart);

        assertEquals(-1, result);
        verify(stationsCache, times(1)).addStationToCache(stationNamePart, 456);
    }
}