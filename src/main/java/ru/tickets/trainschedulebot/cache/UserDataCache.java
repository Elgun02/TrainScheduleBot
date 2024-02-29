package ru.tickets.trainschedulebot.cache;

import org.springframework.stereotype.Service;
import ru.tickets.trainschedulebot.botApi.handlers.state.BotState;
import ru.tickets.trainschedulebot.botApi.handlers.trainsearch.TrainSearchRequestData;
import ru.tickets.trainschedulebot.model.Train;

import java.util.*;

/**
 * The {@code UserDataCache} class is an implementation of the {@link DataCache} interface
 * that serves as a caching mechanism for storing and retrieving user-specific data related to
 * the Train Schedule Bot. It maintains information about the current bot state for users, user-specific
 * train search data, and the list of trains found during a search.
 * <p>
 * This class is annotated with {@link Service} to indicate that it is a Spring service component
 * that can be automatically detected and managed by the Spring framework.
 *
 * @author Elgun Dilanchiev
 * @version 1.0
 * @since 2024-02-29
 */
@Service
public class UserDataCache implements DataCache {

    /**
     * Map to store the current bot state for each user identified by their unique user ID.
     */
    private final Map<Long, BotState> usersBotStates = new HashMap<>();

    /**
     * Map to store user-specific train search data for each user identified by their unique user ID.
     */
    private final Map<Long, TrainSearchRequestData> trainSearchUsersData = new HashMap<>();

    /**
     * Map to store the list of trains found during a search for each chat identified by its unique chat ID.
     */
    private final Map<Long, List<Train>> searchFoundedTrains = new HashMap<>();

    /**
     * Sets the current bot state for the specified user.
     *
     * @param userId    The unique identifier of the user.
     * @param botState  The new {@link BotState} to set for the user.
     */
    @Override
    public void setUsersCurrentBotState(long userId, BotState botState) {
        usersBotStates.put(userId, botState);
    }

    /**
     * Retrieves the current bot state for the specified user. If the user's bot state is not
     * found, it defaults to {@link BotState#SHOW_MAIN_MENU}.
     *
     * @param userId The unique identifier of the user.
     * @return The current {@link BotState} of the user.
     */
    @Override
    public BotState getUsersCurrentBotState(long userId) {
        BotState botState = usersBotStates.get(userId);
        if (botState == null) {
            botState = BotState.SHOW_MAIN_MENU;
        }
        return botState;
    }

    /**
     * Saves user-specific train search data for the specified user.
     *
     * @param userId             The unique identifier of the user.
     * @param trainSearchData    The {@link TrainSearchRequestData} containing user-specific search data.
     */
    @Override
    public void saveTrainSearchData(long userId, TrainSearchRequestData trainSearchData) {
        trainSearchUsersData.put(userId, trainSearchData);
    }

    /**
     * Retrieves user-specific train search data for the specified user. If no data is found, it
     * defaults to a new instance of {@link TrainSearchRequestData}.
     *
     * @param userId The unique identifier of the user.
     * @return The {@link TrainSearchRequestData} containing user-specific search data.
     */
    @Override
    public TrainSearchRequestData getUserTrainSearchData(long userId) {
        TrainSearchRequestData trainSearchData = trainSearchUsersData.get(userId);
        if (trainSearchData == null) {
            trainSearchData = new TrainSearchRequestData();
        }
        return trainSearchData;
    }

    /**
     * Saves the list of trains found during a search for the specified chat.
     *
     * @param chatId      The unique identifier of the chat.
     * @param foundTrains The list of {@link Train} objects found during the search.
     */
    @Override
    public void saveSearchFoundedTrains(long chatId, List<Train> foundTrains) {
        searchFoundedTrains.put(chatId, foundTrains);
    }

    /**
     * Retrieves the list of trains found during a search for the specified chat. If no trains are found,
     * it returns an empty list.
     *
     * @param chatId The unique identifier of the chat.
     * @return The list of {@link Train} objects found during the search.
     */
    @Override
    public List<Train> getSearchFoundedTrains(long chatId) {
        List<Train> foundedTrains = searchFoundedTrains.get(chatId);
        return Objects.isNull(foundedTrains) ? Collections.emptyList() : foundedTrains;
    }
}
