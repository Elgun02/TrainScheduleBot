package ru.tickets.trainschedulebot.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ParseQueryDataServiceTest {

    @Mock
    CallbackQuery callbackQuery;

    @InjectMocks
    ParseQueryDataService parseQueryDataService;

    @Test
    void testParseTrainNumberFromSubscribeQuery() {
        when(callbackQuery.getData()).thenReturn("SUBSCRIBE|083M|29.02.2024");
        String trainNumber = parseQueryDataService.parseTrainNumberFromSubscribeQuery(callbackQuery);
        assertEquals("083M", trainNumber);
    }

    @Test
    void testParseDateDepartFromSubscribeQuery() {
        when(callbackQuery.getData()).thenReturn("SUBSCRIBE|083M|29.02.2024");
        String dateDepart = parseQueryDataService.parseDateDepartFromSubscribeQuery(callbackQuery);
        assertEquals("29.02.2024", dateDepart);
    }

    @Test
    void testParseSubscriptionIdFromUnsubscribeQuery() {
        when(callbackQuery.getData()).thenReturn("UNSUBSCRIBE|65e060f3e7e83f41282601e2");
        String subscriptionId = parseQueryDataService.parseSubscriptionIdFromUnsubscribeQuery(callbackQuery);
        assertEquals("65e060f3e7e83f41282601e2", subscriptionId);
    }
}
