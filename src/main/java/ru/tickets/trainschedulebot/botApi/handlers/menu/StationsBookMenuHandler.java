package ru.tickets.trainschedulebot.botApi.handlers.menu;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.tickets.trainschedulebot.botApi.BotState;
import ru.tickets.trainschedulebot.botApi.handlers.InputMessageHandler;
import ru.tickets.trainschedulebot.cache.UserDataCache;
import ru.tickets.trainschedulebot.service.ReplyMessagesService;
import ru.tickets.trainschedulebot.service.StationBookService;


@Component
@RequiredArgsConstructor
public class StationsBookMenuHandler implements InputMessageHandler {
    private final ReplyMessagesService messagesService;
    private final StationBookService stationsBookService;
    private final UserDataCache userDataCache;

    @Override
    public SendMessage handle(Message message) {
        if (userDataCache.getUsersCurrentBotState(Math.toIntExact(message.getFrom().getId())).equals(BotState.STATIONS_SEARCH)) {
            userDataCache.setUsersCurrentBotState(Math.toIntExact(message.getFrom().getId()), BotState.ASK_STATION_NAME_PART);
            return messagesService.getReplyMessage(message.getChatId(), "reply.stationBookMenu.searchHelpMessage");
        }
        return processUsersInput(message);
    }

    @Override
    public BotState getHandlerName() {
        return BotState.STATIONS_SEARCH;
    }

    private SendMessage processUsersInput(Message inputMsg) {
        String usersInput = inputMsg.getText();
        long chatId = inputMsg.getChatId();
        int userId = Math.toIntExact(inputMsg.getFrom().getId());

        SendMessage replyToUser = messagesService.getWarningReplyMessage(chatId, "reply.query.failed");
        BotState botState = userDataCache.getUsersCurrentBotState(userId);

        if (botState.equals(BotState.ASK_STATION_NAME_PART)) {
            replyToUser = stationsBookService.processStationNamePart(chatId, usersInput);
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_STATION_NAME_PART);
        }

        return replyToUser;
    }
}
