package ru.tickets.trainschedulebot.botApi.handlers.callbackquery;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.tickets.trainschedulebot.botApi.handlers.state.UserButtonStatus;
import ru.tickets.trainschedulebot.model.UserTicketsSubscription;
import ru.tickets.trainschedulebot.service.ParseQueryDataService;
import ru.tickets.trainschedulebot.service.ReplyMessagesService;
import ru.tickets.trainschedulebot.service.SendMessageService;
import ru.tickets.trainschedulebot.service.SubscriptionService;
import ru.tickets.trainschedulebot.utils.Emojis;

import java.util.Optional;

/**
 * Handles the callback query related to unsubscribing from train notifications.
 *
 * @author Elgun Dilanchiev
 * @version 1.1
 * @since 2024-02-29
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

    /**
     * Handles the unsubscribe callback query.
     *
     * @param callbackQuery The Telegram callback query received from the user.
     * @return SendMessage object with the response to the callback query.
     */
    @Override
    public SendMessage handleCallbackQuery(CallbackQuery callbackQuery) {
        final long chatId = callbackQuery.getMessage().getChatId();

        final String subscriptionID = parseService.parseSubscriptionIdFromUnsubscribeQuery(callbackQuery);
        Optional<UserTicketsSubscription> optionalUserSubscription = subscriptionService.getUsersSubscriptionById(subscriptionID);
        if (optionalUserSubscription.isEmpty()) {
            return messagesService.getWarningReplyMessage(chatId, "reply.query.train.userHasNoSubscription");
        }

        String trainNumber = subscriptionService.getTrainNumberBySubscriptionId(subscriptionID);
        String dateDepart = subscriptionService.getDateDepartBySubscriptionId(subscriptionID);
        String callbackData = String.format("%s|%s|%s", CallbackQueryType.SUBSCRIBE.name(), trainNumber, dateDepart);

        UserTicketsSubscription userSubscription = optionalUserSubscription.get();
        subscriptionService.deleteUserSubscriptionById(subscriptionID);

        sendMessageService.updateAndSendInlineKeyBoardMessage(callbackQuery,
                String.format("%s", UserButtonStatus.UNSUBSCRIBED),
                callbackData);

        return messagesService.getReplyMessage(chatId, "reply.query.train.unsubscribed", Emojis.SUCCESS_UNSUBSCRIBED, userSubscription.getTrainNumber(), userSubscription.getDateDepart());
    }
}