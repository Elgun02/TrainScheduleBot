package ru.tickets.trainschedulebot.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.tickets.trainschedulebot.model.Car;
import ru.tickets.trainschedulebot.model.UserTicketsSubscription;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionProcessServiceTest {

    @Mock
    private SubscriptionService subscriptionService;

    @InjectMocks
    private SubscriptionProcessService subscriptionProcessService;

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
    void testProcessAllUsersSubscriptions() {
        List<UserTicketsSubscription> subscriptions = List.of(
                subscription1,subscription2);
        when(subscriptionService.getAllSubscriptions()).thenReturn(subscriptions);
        subscriptionProcessService.processAllUsersSubscriptions();
        verify(subscriptionService, times(1)).getAllSubscriptions();
    }
}