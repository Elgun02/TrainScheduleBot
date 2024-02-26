package ru.tickets.trainschedulebot.botApi.handlers.menu;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.tickets.trainschedulebot.botApi.handlers.InputMessageHandler;
import ru.tickets.trainschedulebot.botApi.handlers.callbackquery.CallbackQueryType;
import ru.tickets.trainschedulebot.botApi.handlers.state.BotState;
import ru.tickets.trainschedulebot.botApi.handlers.state.UserButtonStatus;
import ru.tickets.trainschedulebot.cache.UserDataCache;
import ru.tickets.trainschedulebot.model.Car;
import ru.tickets.trainschedulebot.model.UserTicketsSubscription;
import ru.tickets.trainschedulebot.service.ReplyMessagesService;
import ru.tickets.trainschedulebot.service.SendMessageService;
import ru.tickets.trainschedulebot.service.SubscriptionService;
import ru.tickets.trainschedulebot.utils.Emojis;

import java.util.List;


@Component
@RequiredArgsConstructor
public class SubscriptionsMenuHandler implements InputMessageHandler {
    private final SubscriptionService subscribeService;
    private final SendMessageService sendMessageService;
    private final UserDataCache userDataCache;
    private final ReplyMessagesService messagesService;

    @Override
    public SendMessage handle(Message message) {
        List<UserTicketsSubscription> usersSubscriptions = subscribeService.getUsersSubscriptions(message.getChatId());

        if (usersSubscriptions.isEmpty()) {
            userDataCache.setUsersCurrentBotState(Math.toIntExact(message.getFrom().getId()), BotState.SHOW_MAIN_MENU);
            return messagesService.getReplyMessage(message.getChatId(), "reply.subscriptions.userHasNoSubscriptions");
        }

        sendInlineKeyboardMessage(usersSubscriptions, message);

        return messagesService.getSuccessReplyMessage(message.getChatId(), "reply.subscriptions.listLoaded");
    }

    private void sendInlineKeyboardMessage(List<UserTicketsSubscription> usersSubscriptions, Message message) {
        for (UserTicketsSubscription subscription : usersSubscriptions) {
            StringBuilder carsInfo = new StringBuilder();
            List<Car> cars = subscription.getSubscribedCars();

            for (Car car : cars) {
                carsInfo.append(getCarInfo(car));
            }

            String subscriptionInfo = getSubscriptionInfo(subscription, carsInfo);
            String unsubscribeCallbackData = getUnsubscribeCallbackData(subscription);

            sendMessageService.sendInlineKeyBoardMessage(message.getChatId(), subscriptionInfo, UserButtonStatus.SUBSCRIBED.toString(), unsubscribeCallbackData);
        }
    }

    private String getSubscriptionInfo(UserTicketsSubscription subscription, StringBuilder carsInfo) {
        return messagesService.getReplyText("subscription.trainTicketsInfo",
                Emojis.TRAIN, subscription.getTrainNumber(), subscription.getTrainName(),
                subscription.getStationDepart(),subscription.getDateDepart(), subscription.getTimeDepart(), subscription.getStationArrival(),
                subscription.getDateArrival(), subscription.getTimeArrival(), carsInfo);
    }

    private String getCarInfo(Car car) {
        return messagesService.getReplyText("subscription.carsTicketsInfo", Emojis.BED,
                car.getCarType(), Emojis.MINUS, car.getFreeSeats(), Emojis.MINUS, car.getMinimalPrice());
    }

    private String getUnsubscribeCallbackData(UserTicketsSubscription subscription) {
        return String.format("%s|%s", CallbackQueryType.UNSUBSCRIBE, subscription.getId());
    }

    @Override
    public BotState getHandlerName() {
        return BotState.SHOW_SUBSCRIPTIONS;
    }
}