package ru.tickets.trainschedulebot.botApi;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.tickets.trainschedulebot.botApi.handlers.callbackquery.CallbackQueryFacade;
import ru.tickets.trainschedulebot.botApi.state.BotState;
import ru.tickets.trainschedulebot.botApi.state.BotStateContext;
import ru.tickets.trainschedulebot.cache.UserDataCache;

/**
 * @author Elgun Dilanchiev
 */

@Service
@Slf4j
public class TelegramFacade {
    private final UserDataCache userDataCache;
    private final BotStateContext botStateContext;
    private final CallbackQueryFacade callbackQueryFacade;

    public TelegramFacade(UserDataCache userDataCache, BotStateContext botStateContext,
                          CallbackQueryFacade callbackQueryFacade) {
        this.userDataCache = userDataCache;
        this.botStateContext = botStateContext;
        this.callbackQueryFacade = callbackQueryFacade;
    }

    public SendMessage handleUpdate(Update update) {
        SendMessage replyMessage = null;

        if (update.hasCallbackQuery()) {
            log.info("New callbackQuery from User: {} with data: {}", update.getCallbackQuery().getFrom().getUserName(),
                    update.getCallbackQuery().getData());
            return callbackQueryFacade.processCallbackQuery(update.getCallbackQuery());
        }

        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            log.info("New message from User:{}, chatId: {},  with text: {}",
                    message.getFrom().getUserName(), message.getChatId(), message.getText());
            replyMessage = handleInputMessage(message);
        }

        return replyMessage;
    }

    private SendMessage handleInputMessage(Message message) {
        String inputMsg = message.getText();
        long userId = message.getFrom().getId();
        BotState botState;
        SendMessage replyMessage;

        botState = switch (inputMsg) {
            case "Find Trains" -> BotState.TRAINS_SEARCH;
            case "My Subscriptions" -> BotState.SHOW_SUBSCRIPTIONS_MENU;
            case "Station Directory" -> BotState.STATIONS_SEARCH;
            case "Help" -> BotState.SHOW_HELP_MENU;
            default -> userDataCache.getUsersCurrentBotState(Math.toIntExact(userId));
        };

        userDataCache.setUsersCurrentBotState((int) userId, botState);

        replyMessage = botStateContext.processInputMessage(botState, message);

        return replyMessage;
    }

}
