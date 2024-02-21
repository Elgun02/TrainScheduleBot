package ru.tickets.trainschedulebot.botApi.handlers.menu;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.tickets.trainschedulebot.botApi.state.BotState;
import ru.tickets.trainschedulebot.botApi.TelegramBot;
import ru.tickets.trainschedulebot.botApi.handlers.InputMessageHandler;
import ru.tickets.trainschedulebot.botApi.handlers.callbackquery.CallbackQueryType;
import ru.tickets.trainschedulebot.cache.UserDataCache;
import ru.tickets.trainschedulebot.model.RailwayCarriage;
import ru.tickets.trainschedulebot.model.UserTicketsSubscription;
import ru.tickets.trainschedulebot.service.ReplyMessagesService;
import ru.tickets.trainschedulebot.service.UserTicketsSubscriptionService;
import ru.tickets.trainschedulebot.utils.Emojis;

import java.util.List;

@Component
public class SubscriptionsMenuHandler implements InputMessageHandler {
    private final UserTicketsSubscriptionService subscribeService;
    private final TelegramBot telegramBot;
    private final UserDataCache userDataCache;
    private final ReplyMessagesService messagesService;

    public SubscriptionsMenuHandler(UserTicketsSubscriptionService subscribeService,
                                    UserDataCache userDataCache,
                                    ReplyMessagesService messagesService,
                                    @Lazy TelegramBot telegramBot) {
        this.subscribeService = subscribeService;
        this.messagesService = messagesService;
        this.userDataCache = userDataCache;
        this.telegramBot = telegramBot;
    }

    @Override
    public SendMessage handle(Message message) {
        List<UserTicketsSubscription> usersSubscriptions = subscribeService.getUsersSubscriptions(message.getChatId());

        if (usersSubscriptions.isEmpty()) {
            userDataCache.setUsersCurrentBotState(Math.toIntExact(message.getFrom().getId()), BotState.SHOW_MAIN_MENU);
            return messagesService.getReplyMessage(message.getChatId(), "reply.subscriptions.userHasNoSubscriptions");
        }

        for (UserTicketsSubscription subscription : usersSubscriptions) {
            StringBuilder carsInfo = new StringBuilder();
            List<RailwayCarriage> cars = subscription.getSubscribedCarriages();

            for (RailwayCarriage carriage : cars) {
                carsInfo.append(messagesService.getReplyText("subscription.carsTicketsInfo",
                        carriage.getCarType(), carriage.getFreeSeats(), carriage.getMinimalPrice()));
            }

            String subscriptionInfo = messagesService.getReplyText("subscriptionMenu.trainTicketsInfo",
                    Emojis.TRAIN, subscription.getTrainNumber(), subscription.getTrainName(),
                    subscription.getStationDepart(), subscription.getTimeDepart(), subscription.getStationArrival(),
                    subscription.getTimeArrival(), Emojis.TIME_DEPART, subscription.getDateDepart(),
                    subscription.getDateArrival(), carsInfo);

            String unsubscribeData = String.format("%s|%s", CallbackQueryType.UNSUBSCRIBE, subscription.getId());
            telegramBot.sendInlineKeyBoardMessage(message.getChatId(), subscriptionInfo, "Unsubscribe", unsubscribeData);
        }

        userDataCache.setUsersCurrentBotState(Math.toIntExact(message.getFrom().getId()), BotState.SHOW_MAIN_MENU);

        return messagesService.getSuccessReplyMessage(message.getChatId(), "reply.subscriptions.listLoaded");
    }

    @Override
    public BotState getHandlerName() {
        return BotState.SHOW_SUBSCRIPTIONS_MENU;
    }


}
