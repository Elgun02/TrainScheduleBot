package ru.tickets.trainschedulebot.botApi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.tickets.trainschedulebot.botApi.handlers.callbackquery.CallbackQueryFacade;
import ru.tickets.trainschedulebot.botApi.handlers.state.BotState;
import ru.tickets.trainschedulebot.botApi.handlers.state.BotStateContext;
import ru.tickets.trainschedulebot.cache.UserDataCache;

/**
 * @author Elgun Dilanchiev
 */

@Service
@Slf4j
@RequiredArgsConstructor
public class TelegramFacade {
    private final UserDataCache userDataCache;
    private final BotStateContext botStateContext;
    private final CallbackQueryFacade callbackQueryFacade;

    public SendMessage handleUpdate(Update update) {
        SendMessage replyMessage = null;
        Message message = update.getMessage();

        if (update.hasCallbackQuery()) {
            return handleCallbackQuery(update.getCallbackQuery());
        }

        if (message != null && message.hasText()) {
            replyMessage = handleInputMessage(message);
        }

        return replyMessage;
    }

    private SendMessage handleCallbackQuery(CallbackQuery callbackQuery) {
        log.info("New callbackQuery from User: {} with callback data: {}", callbackQuery.getFrom().getUserName(),
                callbackQuery.getData());
        return callbackQueryFacade.handleCallbackQuery(callbackQuery);
    }

    private SendMessage handleInputMessage(Message message) {
        log.info("New message from User:{}, with text: {}", message.getFrom().getUserName(), message.getText());

        long userId = message.getFrom().getId();
        String inputMsg = message.getText();
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
