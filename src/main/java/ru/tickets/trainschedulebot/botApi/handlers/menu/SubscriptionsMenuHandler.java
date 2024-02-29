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

/**
 * Handles user requests related to subscriptions. Displays a list of subscribed trains and cars
 * through an inline keyboard. If the user has no subscriptions, redirects to the main menu.
 *
 * @author Elgun Dilanchiev
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
public class SubscriptionsMenuHandler implements InputMessageHandler {
    private final SubscriptionService subscribeService;
    private final SendMessageService sendMessageService;
    private final UserDataCache userDataCache;
    private final ReplyMessagesService messagesService;

    /**
     * Handles the user's request to view their subscriptions. Sends an inline keyboard with a list of subscribed trains and cars.
     * If the user has no subscriptions, redirects to the main menu.
     *
     * @param message The Telegram message received from the user.
     * @return SendMessage object with the response to the user's request.
     */
    @Override
    public SendMessage handle(Message message) {
        List<UserTicketsSubscription> usersSubscriptions = subscribeService.getUsersSubscriptionsByChatId(message.getChatId());

        if (usersSubscriptions.isEmpty()) {
            userDataCache.setUsersCurrentBotState(Math.toIntExact(message.getFrom().getId()), BotState.SHOW_MAIN_MENU);
            return messagesService.getReplyMessage(message.getChatId(), "reply.subscriptions.userHasNoSubscriptions");
        }

        sendInlineKeyboardMessage(usersSubscriptions, message);

        return messagesService.getSuccessReplyMessage(message.getChatId(), "reply.subscriptions.listLoaded");
    }

    /**
     * Sends an inline keyboard message to the user with a list of subscribed trains and cars.
     *
     * @param usersSubscriptions List of UserTicketsSubscription representing the user's subscriptions.
     * @param message             The Telegram message received from the user.
     */
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

    /**
     * Constructs a string with information about the subscribed train and cars.
     *
     * @param subscription The UserTicketsSubscription object representing the user's subscription.
     * @param carsInfo     StringBuilder containing information about the subscribed cars.
     * @return String containing information about the subscribed train and cars.
     */
    private String getSubscriptionInfo(UserTicketsSubscription subscription, StringBuilder carsInfo) {
        return messagesService.getReplyText("subscription.trainTicketsInfo",
                Emojis.TRAIN, subscription.getTrainNumber(), subscription.getTrainName(),
                subscription.getStationDepart(),subscription.getDateDepart(), subscription.getTimeDepart(), subscription.getStationArrival(),
                subscription.getDateArrival(), subscription.getTimeArrival(), carsInfo);
    }

    /**
     * Constructs a string with information about a subscribed car.
     *
     * @param car The Car object representing the subscribed car.
     * @return String containing information about the subscribed car.
     */
    private String getCarInfo(Car car) {
        return messagesService.getReplyText("subscription.carsTicketsInfo", Emojis.BED,
                car.getCarType(), Emojis.MINUS, car.getFreeSeats(), Emojis.MINUS, car.getMinimalPrice());
    }

    /**
     * Generates callback data for unsubscribing from a subscription.
     *
     * @param subscription The UserTicketsSubscription object representing the user's subscription.
     * @return String containing the callback data for unsubscribing.
     */
    private String getUnsubscribeCallbackData(UserTicketsSubscription subscription) {
        return String.format("%s|%s", CallbackQueryType.UNSUBSCRIBE, subscription.getId());
    }

    /**
     * Gets the handler's name representing the state.
     *
     * @return BotState representing the handler's name.
     */
    @Override
    public BotState getHandlerName() {
        return BotState.SHOW_SUBSCRIPTIONS;
    }
}