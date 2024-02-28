package ru.tickets.trainschedulebot.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.tickets.trainschedulebot.model.Train;
import ru.tickets.trainschedulebot.model.UserTicketsSubscription;
import java.util.List;

/**
 * Repository interface for managing user subscriptions to train tickets.
 * This interface extends Spring Data MongoDB's {@code MongoRepository}.
 * <p>
 * The repository is responsible for CRUD operations on {@link UserTicketsSubscription} entities.
 *
 * @author Elgun Dilanchiev
 * @version 1.0
 * @since 2024-02-28
 */
@Repository
public interface UserSubscriptionRepository extends MongoRepository<UserTicketsSubscription, String> {
    /**
     * Retrieves a list of user subscriptions based on the provided chat ID.
     *
     * @param id The chat ID associated with the user.
     * @return A list of user subscriptions with the given chat ID.
     */
    List<UserTicketsSubscription> findByChatId(Long id);

    /**
     * Retrieves a user subscription based on the provided train number and departure date.
     *
     * @param trainNumber The train number of the subscribed train.
     * @param dateDepart  The departure date of the subscribed train.
     * @return The user subscription matching the train number and departure date.
     */
    UserTicketsSubscription findByTrainNumberAndDateDepart(String trainNumber, String dateDepart);

    /**
     * Retrieves a list of user subscriptions based on the provided chat ID, train number, and departure date.
     *
     * @param chatId      The chat ID associated with the user.
     * @param trainNumber The train number of the subscribed train.
     * @param dateDepart  The departure date of the subscribed train.
     * @return A list of user subscriptions with the given chat ID, train number, and departure date.
     */
    List<UserTicketsSubscription> findByChatIdAndTrainNumberAndDateDepart(Long chatId, String trainNumber, String dateDepart);
}