package ru.tickets.trainschedulebot.botApi.handlers.callbackquery;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

/**
 * Interface for handling Telegram callback queries.
 *
 * @author Elgun Dilanchiev
 * @version 1.0
 * @since 2024-02-29
 */
public interface CallbackQueryHandler {

    /**
     * Handles the incoming callback query from a user.
     *
     * @param callbackQuery The Telegram callback query received from the user.
     * @return SendMessage object with the response to the callback query.
     */
    SendMessage handleCallbackQuery(CallbackQuery callbackQuery);

    /**
     * Gets the type of callback query that this handler is designed to handle.
     *
     * @return CallbackQueryType representing the type of callback query.
     */
    CallbackQueryType getHandlerQueryType();
}
