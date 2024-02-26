package ru.tickets.trainschedulebot.botApi.handlers.state;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.tickets.trainschedulebot.botApi.handlers.InputMessageHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Defines message hand;ers for each state.
 *
 * @author Elgun Dilanchiev
 */

@Component
public class BotStateContext {
    private final Map<BotState, InputMessageHandler> messageHandlers = new HashMap<>();

    public BotStateContext(List<InputMessageHandler> messageHandlers) {
        messageHandlers.forEach(handler -> this.messageHandlers.put(handler.getHandlerName(), handler));
    }

    public SendMessage handleInputMessage(BotState currentState, Message message) {
        InputMessageHandler currentMessageHandler = findMessageHandler(currentState);
        return currentMessageHandler.handle(message);
    }

    private InputMessageHandler findMessageHandler(BotState currentState) {
        if (isTrainSearchState(currentState)) {
            return messageHandlers.get(BotState.TRAINS_SEARCH);
        }

        if (isStationSearchState(currentState)) {
            return messageHandlers.get(BotState.STATIONS_SEARCH);
        }

        return messageHandlers.get(currentState);
    }

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