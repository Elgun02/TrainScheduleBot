package ru.tickets.trainschedulebot.cache;

import org.springframework.stereotype.Service;
import ru.tickets.trainschedulebot.botApi.handlers.state.BotState;
import ru.tickets.trainschedulebot.botApi.handlers.trainsearch.TrainSearchRequestData;
import ru.tickets.trainschedulebot.model.Train;

import java.util.*;

/**
 * @author Elgun Dilanchiev
 */

@Service
public class UserDataCache implements DataCache {
    private final Map<Long, BotState> usersBotStates = new HashMap<>();
    private final Map<Long, TrainSearchRequestData> trainSearchUsersData = new HashMap<>();
    private final Map<Long, List<Train>> searchFoundedTrains = new HashMap<>();

    @Override
    public void setUsersCurrentBotState(long userId, BotState botState) {
        usersBotStates.put(userId, botState);
    }

    @Override
    public BotState getUsersCurrentBotState(long userId) {
        BotState botState = usersBotStates.get(userId);
        if (botState == null) {
            botState = BotState.SHOW_MAIN_MENU;
        }

        return botState;
    }

    @Override
    public void saveTrainSearchData(long userId, TrainSearchRequestData trainSearchData) {
        trainSearchUsersData.put(userId, trainSearchData);
    }

    @Override
    public TrainSearchRequestData getUserTrainSearchData(long userId) {
        TrainSearchRequestData trainSearchData = trainSearchUsersData.get(userId);
        if (trainSearchData == null) {
            trainSearchData = new TrainSearchRequestData();
        }

        return trainSearchData;
    }

    @Override
    public void saveSearchFoundedTrains(long chatId, List<Train> foundTrains) {
        searchFoundedTrains.put(chatId, foundTrains);
    }

    @Override
    public List<Train> getSearchFoundedTrains(long chatId) {
        List<Train> foundedTrains = searchFoundedTrains.get(chatId);

        return Objects.isNull(foundedTrains) ? Collections.emptyList() : foundedTrains;
    }

}
