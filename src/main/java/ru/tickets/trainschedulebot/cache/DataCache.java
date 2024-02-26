package ru.tickets.trainschedulebot.cache;

import ru.tickets.trainschedulebot.botApi.handlers.state.BotState;
import ru.tickets.trainschedulebot.botApi.handlers.trainsearch.TrainSearchRequestData;
import ru.tickets.trainschedulebot.model.Train;

import java.util.List;

/**
 * @author Elgun Dilanchiev
 */

public interface DataCache {
    void setUsersCurrentBotState(long userId, BotState botState);

    BotState getUsersCurrentBotState(long userId);

    void saveTrainSearchData(long userId, TrainSearchRequestData trainSearchData);

    TrainSearchRequestData getUserTrainSearchData(long userId);

    void saveSearchFoundedTrains(long chatId, List<Train> foundTrains);

    List<Train> getSearchFoundedTrains(long chatId);
}
