package ru.tickets.trainschedulebot.botApi.handlers;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.tickets.trainschedulebot.botApi.handlers.state.BotState;

/**
 * Interface defining a contract for handling user input messages in the Telegram bot.
 * Implementing classes should provide logic for processing different types of user input.
 * Each handler is associated with a specific bot state, which determines its behavior.
 *
 * @author Elgun Dilanchiev
 * @version 1.0
 * @since 2024-02-29
 */
public interface InputMessageHandler {
    /**
     * Handles the incoming message and returns a SendMessage object as a response.
     *
     * @param message The incoming message from the user.
     * @return SendMessage object containing the response to be sent back to the user.
     */
    SendMessage handle(Message message);

    /**
     * Gets the name of the handler, which corresponds to a specific bot state.
     *
     * @return BotState representing the name of the handler.
     */
    BotState getHandlerName();
}
