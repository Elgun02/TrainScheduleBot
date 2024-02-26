package ru.tickets.trainschedulebot.cache;

import ru.tickets.trainschedulebot.botApi.handlers.state.BotState;
import ru.tickets.trainschedulebot.botApi.handlers.trainsearch.TrainSearchRequestData;
import ru.tickets.trainschedulebot.model.Train;

import java.util.List;

/**
 * @author Elgun Dilanchiev
 */

public interface DataCache {
    BotState getUsersCurrentBotState(long userId);
    List<Train> getSearchFoundedTrains(long chatId);
    TrainSearchRequestData getUserTrainSearchData(long userId);
    void setUsersCurrentBotState(long userId, BotState botState);
    void saveSearchFoundedTrains(long chatId, List<Train> foundTrains);
    void saveTrainSearchData(long userId, TrainSearchRequestData trainSearchData);
}