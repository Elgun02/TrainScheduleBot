package ru.tickets.trainschedulebot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
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
            log.error("Ошибка при сохранение подписки: {}", e.getMessage(), e);
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

    public String getByTrainNumberAndDateDepart(String trainNumber, String dateDepart) {
        UserTicketsSubscription user = subscriptionsRepository.findByTrainNumberAndDateDepart(trainNumber, dateDepart);

        return user.getId();
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
}
