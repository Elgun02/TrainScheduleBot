package ru.tickets.trainschedulebot.botApi.handlers.callbackquery;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.tickets.trainschedulebot.botApi.TelegramBot;
import ru.tickets.trainschedulebot.model.UserTicketsSubscription;
import ru.tickets.trainschedulebot.service.ParseQueryDataService;
import ru.tickets.trainschedulebot.service.ReplyMessagesService;
import ru.tickets.trainschedulebot.service.SendMessageService;
import ru.tickets.trainschedulebot.service.SubscriptionService;
import ru.tickets.trainschedulebot.utils.Emojis;

import java.util.Optional;

/**
 * Обрабатывает запрос "Отписаться" от уведомлений по ценам.
 *
 * @author Sergei Viacheslaev
 */
@Component
@RequiredArgsConstructor
public class UnsubscribeQueryHandler implements CallbackQueryHandler {
    private static final CallbackQueryType HANDLER_QUERY_TYPE = CallbackQueryType.UNSUBSCRIBE;
    private final SubscriptionService subscriptionService;
    private final ParseQueryDataService parseService;
    private final ReplyMessagesService messagesService;
    private final SendMessageService sendMessageService;

    @Override
    public CallbackQueryType getHandlerQueryType() {
        return HANDLER_QUERY_TYPE;
    }

    @Override
    public SendMessage handleCallbackQuery(CallbackQuery callbackQuery) {
        final long chatId = callbackQuery.getMessage().getChatId();

        final String subscriptionID = parseService.parseSubscriptionIdFromUnsubscribeQuery(callbackQuery);
        System.out.println("ID +++ = " + subscriptionID);
        Optional<UserTicketsSubscription> optionalUserSubscription = subscriptionService.getUsersSubscriptionById(subscriptionID);
        if (optionalUserSubscription.isEmpty()) {
            return messagesService.getWarningReplyMessage(chatId, "reply.query.train.userHasNoSubscription");
        }

        String trainNumber = subscriptionService.getTrainNumberBySubscriptionId(subscriptionID);
        String dateDepart = subscriptionService.getDateDepartBySubscriptionId(subscriptionID);
        String callbackData = String.format("%s|%s|%s", CallbackQueryType.SUBSCRIBE.name(), trainNumber, dateDepart);

        UserTicketsSubscription userSubscription = optionalUserSubscription.get();
        subscriptionService.deleteUserSubscription(subscriptionID);

        sendMessageService.updateAndSendInlineKeyBoardMessage(callbackQuery,
                String.format("%s", UserButtonStatus.UNSUBSCRIBED),
                callbackData);

        return messagesService.getReplyMessage(chatId, "reply.query.train.unsubscribed", Emojis.SUCCESS_UNSUBSCRIBED, userSubscription.getTrainNumber(), userSubscription.getDateDepart());
    }


}
