package ru.tickets.trainschedulebot.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

/**
 * Service class for parsing data from Telegram callback queries.
 * This class provides methods to extract specific information from callback queries.
 *
 * @author Elgun Dilanchiev
 * @version 1.0
 * @since 2024-02-28
 */
@Service
public class ParseQueryDataService {

    /**
     * Parses the train number from a subscribe query in a Telegram callback.
     *
     * @param callbackQuery The CallbackQuery containing the necessary data.
     * @return The parsed train number from the subscribe query.
     */
    public String parseTrainNumberFromSubscribeQuery(CallbackQuery callbackQuery) {
        return callbackQuery.getData().split("\\|")[1];
    }

    /**
     * Parses the departure date from a subscribe query in a Telegram callback.
     *
     * @param callbackQuery The CallbackQuery containing the necessary data.
     * @return The parsed departure date from the subscribe query.
     */
    public String parseDateDepartFromSubscribeQuery(CallbackQuery callbackQuery) {
        return callbackQuery.getData().split("\\|")[2];
    }

    /**
     * Parses the subscription ID from an unsubscribe query in a Telegram callback.
     *
     * @param callbackQuery The CallbackQuery containing the necessary data.
     * @return The parsed subscription ID from the unsubscribe query.
     */
    public String parseSubscriptionIdFromUnsubscribeQuery(CallbackQuery callbackQuery) {
        return callbackQuery.getData().split("\\|")[1];
    }
}