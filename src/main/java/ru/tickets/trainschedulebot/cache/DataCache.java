package ru.tickets.trainschedulebot.cache;

import ru.tickets.trainschedulebot.botApi.handlers.state.BotState;
import ru.tickets.trainschedulebot.botApi.handlers.trainsearch.TrainSearchRequestData;
import ru.tickets.trainschedulebot.model.Train;

import java.util.List;

/**
 * The {@code DataCache} interface represents a caching mechanism for storing and retrieving
 * data related to the Train Schedule Bot. It is responsible for managing user states, storing
 * search results, and retaining user-specific train search data.
 * <p>
 * This interface provides methods to interact with the cache, such as retrieving the current
 * bot state for a user, obtaining a list of trains found during a search, and saving user-specific
 * train search data.
 * <p>
 * Implementations of this interface should handle the storage and retrieval of data in a manner
 * suitable for the underlying storage mechanism, whether it be in-memory storage, a database, or
 * any other persistent storage.
 *
 * @author Elgun Dilanchiev
 * @version 1.0
 * @since 2024-02-29
 */
public interface DataCache {

    /**
     * Retrieves the current bot state for the specified user.
     *
     * @param userId The unique identifier of the user.
     * @return The current {@link BotState} of the user.
     */
    BotState getUsersCurrentBotState(long userId);

    /**
     * Retrieves the list of trains found during a search for the specified chat.
     *
     * @param chatId The unique identifier of the chat.
     * @return The list of {@link Train} objects found during the search.
     */
    List<Train> getSearchFoundedTrains(long chatId);

    /**
     * Retrieves the user-specific train search data for the specified user.
     *
     * @param userId The unique identifier of the user.
     * @return The {@link TrainSearchRequestData} containing user-specific search data.
     */
    TrainSearchRequestData getUserTrainSearchData(long userId);

    /**
     * Sets the current bot state for the specified user.
     *
     * @param userId    The unique identifier of the user.
     * @param botState  The new {@link BotState} to set for the user.
     */
    void setUsersCurrentBotState(long userId, BotState botState);

    /**
     * Saves the list of trains found during a search for the specified chat.
     *
     * @param chatId      The unique identifier of the chat.
     * @param foundTrains The list of {@link Train} objects found during the search.
     */
    void saveSearchFoundedTrains(long chatId, List<Train> foundTrains);

    /**
     * Saves the user-specific train search data for the specified user.
     *
     * @param userId             The unique identifier of the user.
     * @param trainSearchData    The {@link TrainSearchRequestData} containing user-specific search data.
     */
    void saveTrainSearchData(long userId, TrainSearchRequestData trainSearchData);
}
