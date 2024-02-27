package ru.tickets.trainschedulebot.botApi.handlers.callbackquery;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.tickets.trainschedulebot.service.ReplyMessagesService;

import java.util.List;
import java.util.Optional;

/**
 * Facade class for handling Telegram callback queries.
 *
 * @author Elgun Dilanchiev
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
public class CallbackQueryFacade {
    private final ReplyMessagesService messagesService;
    private final List<CallbackQueryHandler> callbackQueryHandlers;

    /**
     * Handles the incoming callback query from a user.
     *
     * @param usersQuery The Telegram callback query received from the user.
     * @return SendMessage object with the response to the callback query.
     */
    public SendMessage handleCallbackQuery(CallbackQuery usersQuery) {
        CallbackQueryType usersQueryType = CallbackQueryType.valueOf(usersQuery.getData().split("\\|")[0]);

        Optional<CallbackQueryHandler> queryHandler = callbackQueryHandlers.stream().
                filter(callbackQuery -> callbackQuery.getHandlerQueryType().equals(usersQueryType)).findFirst();

        return queryHandler.map(handler -> handler.handleCallbackQuery(usersQuery)).
                orElse(messagesService.getWarningReplyMessage(usersQuery.getMessage().getChatId(), "reply.query.failed"));
    }
}