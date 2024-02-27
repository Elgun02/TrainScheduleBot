package ru.tickets.trainschedulebot.botApi.handlers.menu;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.tickets.trainschedulebot.botApi.handlers.state.BotState;
import ru.tickets.trainschedulebot.botApi.handlers.InputMessageHandler;
import ru.tickets.trainschedulebot.cache.UserDataCache;
import ru.tickets.trainschedulebot.service.ReplyMessagesService;
import ru.tickets.trainschedulebot.service.StationBookService;


/**
 * Handles user requests related to station book menu.
 *
 * @author Elgun Dilanchiev
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
public class StationsBookMenuHandler implements InputMessageHandler {
    private final ReplyMessagesService messagesService;
    private final StationBookService stationsBookService;
    private final UserDataCache userDataCache;

    /**
     * Handles the user's message regarding station book menu.
     *
     * @param message The Telegram message received from the user.
     * @return SendMessage object with the response to the user's message.
     */
    @Override
    public SendMessage handle(Message message) {
        if (userDataCache.getUsersCurrentBotState(Math.toIntExact(message.getFrom().getId())).equals(BotState.STATIONS_SEARCH)) {
            userDataCache.setUsersCurrentBotState(Math.toIntExact(message.getFrom().getId()), BotState.ASK_STATION_NAME_PART);

            return messagesService.getReplyMessage(message.getChatId(), "reply.stationBookMenu.searchHelpMessage");
        }
        return processUsersInput(message);
    }

    /**
     * Gets the handler's name representing the state.
     *
     * @return BotState representing the handler's name.
     */
    @Override
    public BotState getHandlerName() {
        return BotState.STATIONS_SEARCH;
    }

    /**
     * Handles the user's input for station name search.
     *
     * @param inputMsg The Telegram message received from the user.
     * @return SendMessage object with the response to the user's input.
     */
    private SendMessage processUsersInput(Message inputMsg) {
        String usersInput = inputMsg.getText();
        long chatId = inputMsg.getChatId();
        long userId = inputMsg.getFrom().getId();

        SendMessage replyToUser = messagesService.getWarningReplyMessage(chatId, "reply.query.failed");
        BotState botState = userDataCache.getUsersCurrentBotState(userId);

        if (botState.equals(BotState.ASK_STATION_NAME_PART)) {
            replyToUser = stationsBookService.processStationNamePart(chatId, usersInput);
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_STATION_NAME_PART);
        }

        return replyToUser;
    }
}