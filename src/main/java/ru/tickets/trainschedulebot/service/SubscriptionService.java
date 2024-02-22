package ru.tickets.trainschedulebot.service;

import org.springframework.stereotype.Service;
import ru.tickets.trainschedulebot.model.UserTicketsSubscription;
import ru.tickets.trainschedulebot.repository.UserSubscriptionRepository;

import java.util.List;
import java.util.Optional;

@Service
public class SubscriptionService {

    private final UserSubscriptionRepository subscriptionsRepository;

    public SubscriptionService(UserSubscriptionRepository repository) {
        this.subscriptionsRepository = repository;
    }

    public List<UserTicketsSubscription> getAllSubscriptions() {
        return subscriptionsRepository.findAll();
    }

    public void saveUserSubscription(UserTicketsSubscription usersSubscription) {
        subscriptionsRepository.save(usersSubscription);
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


}
