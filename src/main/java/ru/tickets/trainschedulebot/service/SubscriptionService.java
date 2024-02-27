package ru.tickets.trainschedulebot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.tickets.trainschedulebot.model.UserTicketsSubscription;
import ru.tickets.trainschedulebot.repository.UserSubscriptionRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final UserSubscriptionRepository subscriptionsRepository;

    public List<UserTicketsSubscription> getAllSubscriptions() {
        return subscriptionsRepository.findAll();
    }

    public void saveUserSubscription(UserTicketsSubscription usersSubscription) {
        try {
            subscriptionsRepository.save(usersSubscription);
        } catch (Exception e) {
            log.error("Error while saving subscription: {}", e.getMessage(), e);
        }
    }

    public void deleteUserSubscription(String subscriptionID) {
        subscriptionsRepository.deleteById(subscriptionID);
    }


    public boolean hasTicketsSubscription(UserTicketsSubscription userSubscription) {
        return !subscriptionsRepository.findByChatIdAndTrainNumberAndDateDepart(userSubscription.getChatId(),
                userSubscription.getTrainNumber(), userSubscription.getDateDepart()).isEmpty();
    }

    public Optional<UserTicketsSubscription> getUsersSubscriptionById(String subscriptionID) {
        return subscriptionsRepository.findById(subscriptionID);
    }

    public List<UserTicketsSubscription> getUsersSubscriptions(long chatId) {
        return subscriptionsRepository.findByChatId(chatId);
    }

    public String getSubscriptionIdByTrainNumberAndDateDepart(String trainNumber, String dateDepart) {
        UserTicketsSubscription subscription = subscriptionsRepository.findByTrainNumberAndDateDepart(trainNumber, dateDepart);

        return subscription.getId();
    }

    public String getTrainNumberBySubscriptionId(String subscriptionId) {
        Optional<UserTicketsSubscription> subscription = getUsersSubscriptionById(subscriptionId);

        String trainNumber = null;
        if (subscription.isPresent()) {
            trainNumber = subscription.get().getTrainNumber();
        }
        return trainNumber;
    }

    public String getDateDepartBySubscriptionId(String subscriptionId) {
        Optional<UserTicketsSubscription> subscription = getUsersSubscriptionById(subscriptionId);

        String dateDepart = null;
        if (subscription.isPresent()) {
            dateDepart = subscription.get().getDateDepart();
        }
        return dateDepart;
    }

    public boolean isUserSubscribed(String trainNumber, String dateDepart) {
        List<UserTicketsSubscription> subscribedTrains = subscriptionsRepository.findAll();

        for (UserTicketsSubscription subscription : subscribedTrains) {
            if (subscription.getTrainNumber().equals(trainNumber) && subscription.getDateDepart().equals(dateDepart)) {
                return true;
            }
        }

        return false;
    }
}
