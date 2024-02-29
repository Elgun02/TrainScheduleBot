package ru.tickets.trainschedulebot.botApi.handlers.state;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.tickets.trainschedulebot.botApi.handlers.InputMessageHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages the context of bot states and corresponding message handlers.
 * The class maintains a mapping of each bot state to its corresponding message handler.
 * It provides methods to handle input messages based on the current bot state.
 *
 * @author Elgun Dilanchiev
 * @version 1.0
 * @since 2024-02-29
 */
@Component
public class BotStateContext {
    private final Map<BotState, InputMessageHandler> messageHandlers = new HashMap<>();

    /**
     * Initializes the BotStateContext with a list of message handlers.
     *
     * @param messageHandlers List of InputMessageHandler implementations to be mapped with bot states.
     */
    public BotStateContext(List<InputMessageHandler> messageHandlers) {
        messageHandlers.forEach(handler -> this.messageHandlers.put(handler.getHandlerName(), handler));
    }

    /**
     * Handles input messages based on the current bot state.
     *
     * @param currentState The current state of the bot.
     * @param message      The input message to be processed.
     * @return SendMessage response based on the handled input.
     */
    public SendMessage handleInputMessage(BotState currentState, Message message) {
        InputMessageHandler currentMessageHandler = findMessageHandler(currentState);
        return currentMessageHandler.handle(message);
    }

    /**
     * Finds the appropriate message handler based on the current bot state.
     *
     * @param currentState The current state of the bot.
     * @return InputMessageHandler corresponding to the current state.
     */
    private InputMessageHandler findMessageHandler(BotState currentState) {
        if (isTrainSearchState(currentState)) {
            return messageHandlers.get(BotState.TRAINS_SEARCH);
        }

        if (isStationSearchState(currentState)) {
            return messageHandlers.get(BotState.STATIONS_SEARCH);
        }

        return messageHandlers.get(currentState);
    }

    /**
     * Checks if the current state corresponds to a train search state.
     *
     * @param currentState The current state of the bot.
     * @return True if the current state is related to train search, otherwise false.
     */
    private Boolean isTrainSearchState(BotState currentState) {
        return switch (currentState) {
            case
                TRAINS_SEARCH,
                ASK_DATE_DEPART,
                DATE_DEPART_RECEIVED,
                ASK_STATION_ARRIVAL,
                ASK_STATION_DEPART,
                TRAINS_SEARCH_STARTED,
                TRAIN_INFO_RESPONSE_AWAITING,
                TRAINS_SEARCH_FINISH -> true;
            default -> false;
        };
    }

    /**
     * Checks if the current state corresponds to a station search state.
     *
     * @param currentState The current state of the bot.
     * @return True if the current state is related to station search, otherwise false.
     */
    private Boolean isStationSearchState(BotState currentState) {
        return switch (currentState) {
            case
                SHOW_STATIONS_BOOK_MENU,
                ASK_STATION_NAME_PART,
                STATION_NAME_PART_RECEIVED,
                STATIONS_SEARCH -> true;
            default -> false;
        };
    }
}