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
 * Service class responsible for handling updates received by the Telegram bot.
 * It delegates the processing of different types of updates (messages, callback queries)
 * to corresponding handlers and provides a central point for managing user data and bot states.
 *
 * @author Elgun Dilanchiev
 * @version 1.0
 * @since 2024-02-29
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class TelegramFacade {
    private final UserDataCache userDataCache;
    private final BotStateContext botStateContext;
    private final CallbackQueryFacade callbackQueryFacade;

    /**
     * Handles the incoming update, which can be a message or a callback query,
     * and delegates the processing to the appropriate handlers.
     *
     * @param update The incoming update from the Telegram API.
     * @return SendMessage object representing the response to be sent back to the user.
     */
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

    /**
     * Handles the incoming callback query and delegates the processing to the CallbackQueryFacade.
     *
     * @param callbackQuery The incoming callback query from the Telegram API.
     * @return SendMessage object representing the response to be sent back to the user.
     */
    private SendMessage handleCallbackQuery(CallbackQuery callbackQuery) {
        log.info("New callbackQuery from User: {} with callback data: {}", callbackQuery.getFrom().getUserName(),
                callbackQuery.getData());
        return callbackQueryFacade.handleCallbackQuery(callbackQuery);
    }

    /**
     * Handles the incoming message and determines the appropriate bot state for further processing.
     * Delegates the processing to the BotStateContext based on the identified bot state.
     *
     * @param message The incoming message from the user.
     * @return SendMessage object representing the response to be sent back to the user.
     */
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
