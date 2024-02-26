package ru.tickets.trainschedulebot.botApi;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.tickets.trainschedulebot.botApi.handlers.callbackquery.CallbackQueryFacade;
import ru.tickets.trainschedulebot.botApi.handlers.state.BotState;
import ru.tickets.trainschedulebot.botApi.handlers.state.BotStateContext;
import ru.tickets.trainschedulebot.cache.UserDataCache;
import ru.tickets.trainschedulebot.service.LocaleMessageService;
import ru.tickets.trainschedulebot.service.ReplyMessagesService;

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
        Message message = update.getMessage();

        if (update.hasCallbackQuery()) {
            log.info("New callbackQuery from User: {} with callback data: {}", update.getCallbackQuery().getFrom().getUserName(),
                    update.getCallbackQuery().getData());

            return callbackQueryFacade.handleCallbackQuery(update.getCallbackQuery());
        }

        if (message != null && message.hasText()) {
            log.info("New message from User:{}, with text: {}", message.getFrom().getUserName(), message.getText());
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
            case "Найти поезда" -> BotState.TRAINS_SEARCH;
            case "Мои подписки" -> BotState.SHOW_SUBSCRIPTIONS;
            case "Справочник ст." -> BotState.STATIONS_SEARCH;
            case "Помощь" -> BotState.SHOW_HELP_MENU;
            default -> userDataCache.getUsersCurrentBotState(userId);
        };

        userDataCache.setUsersCurrentBotState(Math.toIntExact(userId), botState);
        replyMessage = botStateContext.handleInputMessage(botState, message);

        return replyMessage;
    }

}
