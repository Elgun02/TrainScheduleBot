package ru.tickets.trainschedulebot.botApi.handlers;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.tickets.trainschedulebot.botApi.handlers.state.BotState;

/**
 * @author Elgun Dilanchiev
 */
public interface InputMessageHandler {
    SendMessage handle(Message message);
    BotState getHandlerName();
}