package ru.tickets.trainschedulebot.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.tickets.trainschedulebot.model.UserTicketsSubscription;
import java.util.List;

/**
 * @author Elgun Dilanchiev
 */

@Repository
public interface UserTicketsSubscriptionMongoRepository extends MongoRepository<UserTicketsSubscription, String> {

    List<UserTicketsSubscription> findByChatId(Long id);

    List<UserTicketsSubscription> findByChatIdAndTrainNumberAndDateDepart(Long chatId, String trainNumber, String dateDepart);
}
