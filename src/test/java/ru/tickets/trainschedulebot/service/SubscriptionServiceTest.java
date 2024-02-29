package ru.tickets.trainschedulebot.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.tickets.trainschedulebot.model.Car;
import ru.tickets.trainschedulebot.model.UserTicketsSubscription;
import ru.tickets.trainschedulebot.repository.UserSubscriptionRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    @Mock
    private UserSubscriptionRepository subscriptionRepository;

    @InjectMocks
    private SubscriptionService subscriptionService;

    private UserTicketsSubscription subscription1, subscription2;

    @BeforeEach
    void setUp() {
        subscription1 = new UserTicketsSubscription(123456789, "123", "Express", "Station A",
                "Station B", "2024-03-01", "2024-03-02", "08:00", "16:00",
                List.of(new Car("Type1", 100, 5000)));

        subscription2 = new UserTicketsSubscription(987654321, "456", "Fast Train", "Station X",
                "Station Y", "2024-03-03", "2024-03-04", "10:00", "18:00",
                List.of(new Car("Type1", 100, 5000)));
    }

    @Test
    void testGetAllSubscriptions() {
        List<UserTicketsSubscription> subscriptions = List.of(subscription1, subscription2);
        when(subscriptionRepository.findAll()).thenReturn(subscriptions);
        List<UserTicketsSubscription> result = subscriptionService.getAllSubscriptions();
        assertEquals(subscriptions, result);
    }

    @Test
    void testSaveSubscription() {
        subscriptionService.saveUserSubscription(subscription1);
        verify(subscriptionRepository, times(1)).save(subscription1);
    }

    @Test
    void testDeleteUserSubscriptionById() {
        String subscriptionId = "1234e7";
        subscriptionService.deleteUserSubscriptionById(subscriptionId);
        verify(subscriptionRepository, times(1)).deleteById(subscriptionId);
    }

    @Test
    void testHasTicketsSubscription() {
        List<UserTicketsSubscription> subscriptions = List.of(subscription1, subscription2);
        when(subscriptionRepository.findByChatIdAndTrainNumberAndDateDepart(anyLong(), anyString(), anyString()))
                .thenReturn(subscriptions);
        boolean result = subscriptionService.hasTicketsSubscription(subscription1);

        assertTrue(result);
    }

    @Test
    void testGetUsersSubscriptionById() {
        String subscriptionId = "1234e7";
        when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.ofNullable(subscription1));
        Optional<UserTicketsSubscription> result = subscriptionService.getUsersSubscriptionById(subscriptionId);
        assertTrue(result.isPresent());
        assertEquals(subscription1, result.get());
    }

    @Test
    void testGetUserSubscriptions() {
        long chatId = 123456789L;
        List<UserTicketsSubscription> subscriptions = List.of(subscription1);
        when(subscriptionRepository.findByChatId(chatId)).thenReturn(List.of(subscription1));
        List<UserTicketsSubscription> result = subscriptionService.getUsersSubscriptionsByChatId(chatId);
        assertEquals(subscriptions, result);
    }

    @Test
    void testGetSubscriptionIdByTrainNumberAndDateDepart() {
        subscription1.setId("12345e7");
        String trainNumber = subscription1.getTrainNumber();
        String dateDepart = subscription1.getDateDepart();
        when(subscriptionRepository.findByTrainNumberAndDateDepart(trainNumber, dateDepart)).thenReturn(subscription1);
        String result = subscriptionService.getSubscriptionIdByTrainNumberAndDateDepart(trainNumber, dateDepart);
        assertEquals(subscription1.getId(), result);
    }

    @Test
    void testGetDateDepartBySubscriptionId() {
        String subscriptionId = "1234e7";
        String dateDepart = subscription1.getDateDepart();
        when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.of(subscription1));
        String result = subscriptionService.getDateDepartBySubscriptionId(subscriptionId);
        assertEquals(dateDepart, result);
    }

    @Test
    void testIsUserSubscribed() {
        String trainNumber = subscription2.getTrainNumber();
        String dateDepart = subscription2.getDateDepart();
        List<UserTicketsSubscription> subscriptions = List.of(subscription1, subscription2);
        when(subscriptionRepository.findAll()).thenReturn(subscriptions);
        boolean result = subscriptionService.isUserSubscribed(trainNumber, dateDepart);
        assertTrue(result);
    }

    @Test
    void testUserIsBotSubscribed() {
        String trainNumber = "12345";
        String dateDepart = "01.01.2024";
        List<UserTicketsSubscription> subscriptions = List.of(subscription1, subscription2);
        when(subscriptionRepository.findAll()).thenReturn(subscriptions);
        boolean result = subscriptionService.isUserSubscribed(trainNumber, dateDepart);
        assertFalse(result);
    }
}