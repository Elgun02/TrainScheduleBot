package ru.tickets.trainschedulebot.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.tickets.trainschedulebot.cache.StationsDataCache;
import ru.tickets.trainschedulebot.model.TrainStation;
import ru.tickets.trainschedulebot.service.ReplyMessagesService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StationBookServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private StationsDataCache stationsCache;

    @Mock
    private ReplyMessagesService messagesService;

    @InjectMocks
    private StationBookService stationBookService;

    private long chatId;
    private String stationNamePart;

    @BeforeEach
    void setUp() {
        chatId = 123L;
        stationNamePart = "TestStationMoscow".toUpperCase();
    }

    @Test
    void testProcessStationNamePart_StationFoundInCache_ReturnsSendMessage() {
        when(stationsCache.getStationName(stationNamePart)).thenReturn(Optional.of("TestStationMoscow"));
        when(stationBookService.processStationNamePart(chatId, stationNamePart)).thenReturn(new SendMessage("123", stationNamePart));
        SendMessage result = stationBookService.processStationNamePart(chatId, stationNamePart);
        assertEquals(stationNamePart, result.getText());
    }
}