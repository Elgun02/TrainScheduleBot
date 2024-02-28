package ru.tickets.trainschedulebot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.tickets.trainschedulebot.model.UserTicketsSubscription;
import ru.tickets.trainschedulebot.repository.UserSubscriptionRepository;

import java.util.List;
import java.util.Optional;

/**
 * Service class for managing user subscriptions related to train tickets.
 * This class provides methods to retrieve, save, and delete user subscriptions,
 * as well as check for subscription existence and obtain subscription details.
 *
 * @author Elgun Dilanchiev
 * @version 1.0
 * @since 2024-02-28
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final UserSubscriptionRepository subscriptionsRepository;

    /**
     * Retrieves all user subscriptions.
     *
     * @return A list of all user subscriptions.
     */
    public List<UserTicketsSubscription> getAllSubscriptions() {
        return subscriptionsRepository.findAll();
    }

    /**
     * Saves a user subscription to the repository.
     *
     * @param usersSubscription The user subscription to be saved.
     */
    public void saveUserSubscription(UserTicketsSubscription usersSubscription) {
        try {
            subscriptionsRepository.save(usersSubscription);
        } catch (Exception e) {
            log.error("Error while saving subscription: {}", e.getMessage(), e);
        }
    }

    /**
     * Deletes a user subscription based on the subscription ID.
     *
     * @param subscriptionID The ID of the subscription to be deleted.
     */
    public void deleteUserSubscription(String subscriptionID) {
        subscriptionsRepository.deleteById(subscriptionID);
    }

    /**
     * Checks if a user has a subscription for a particular train and date.
     *
     * @param userSubscription The user subscription to check.
     * @return True if the user has a subscription, false otherwise.
     */
    public boolean hasTicketsSubscription(UserTicketsSubscription userSubscription) {
        return !subscriptionsRepository.findByChatIdAndTrainNumberAndDateDepart(userSubscription.getChatId(),
                userSubscription.getTrainNumber(), userSubscription.getDateDepart()).isEmpty();
    }

    /**
     * Retrieves a user subscription by its ID.
     *
     * @param subscriptionID The ID of the subscription to retrieve.
     * @return An optional containing the user subscription, or empty if not found.
     */
    public Optional<UserTicketsSubscription> getUsersSubscriptionById(String subscriptionID) {
        return subscriptionsRepository.findById(subscriptionID);
    }

    /**
     * Retrieves all subscriptions for a specific user based on their chat ID.
     *
     * @param chatId The chat ID of the user.
     * @return A list of user subscriptions for the specified chat ID.
     */
    public List<UserTicketsSubscription> getUsersSubscriptions(long chatId) {
        return subscriptionsRepository.findByChatId(chatId);
    }

    /**
     * Retrieves the subscription ID based on the train number and departure date.
     *
     * @param trainNumber The train number.
     * @param dateDepart  The departure date.
     * @return The subscription ID, or null if not found.
     */
    public String getSubscriptionIdByTrainNumberAndDateDepart(String trainNumber, String dateDepart) {
        UserTicketsSubscription subscription = subscriptionsRepository.findByTrainNumberAndDateDepart(trainNumber, dateDepart);

        return subscription.getId();
    }

    /**
     * Retrieves the train number based on the subscription ID.
     *
     * @param subscriptionId The subscription ID.
     * @return The train number, or null if not found.
     */
    public String getTrainNumberBySubscriptionId(String subscriptionId) {
        Optional<UserTicketsSubscription> subscription = getUsersSubscriptionById(subscriptionId);

        String trainNumber = null;
        if (subscription.isPresent()) {
            trainNumber = subscription.get().getTrainNumber();
        }
        return trainNumber;
    }

    /**
     * Retrieves the departure date based on the subscription ID.
     *
     * @param subscriptionId The subscription ID.
     * @return The departure date, or null if not found.
     */
    public String getDateDepartBySubscriptionId(String subscriptionId) {
        Optional<UserTicketsSubscription> subscription = getUsersSubscriptionById(subscriptionId);

        String dateDepart = null;
        if (subscription.isPresent()) {
            dateDepart = subscription.get().getDateDepart();
        }
        return dateDepart;
    }

    /**
     * Checks if a user is subscribed to a specific train and date.
     *
     * @param trainNumber The train number.
     * @param dateDepart  The departure date.
     * @return True if the user is subscribed, false otherwise.
     */
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
