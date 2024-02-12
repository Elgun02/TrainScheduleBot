package ru.tickets.trainschedulebot.botApi.handlers.callbackquery;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.tickets.trainschedulebot.botApi.TelegramBot;
import ru.tickets.trainschedulebot.cache.UserDataCache;
import ru.tickets.trainschedulebot.model.RailwayCarriage;
import ru.tickets.trainschedulebot.model.Train;
import ru.tickets.trainschedulebot.model.UserTicketsSubscription;
import ru.tickets.trainschedulebot.service.ParseQueryDataService;
import ru.tickets.trainschedulebot.service.ReplyMessagesService;
import ru.tickets.trainschedulebot.service.UserTicketsSubscriptionService;
import ru.tickets.trainschedulebot.utils.Emojis;

import java.util.List;
import java.util.Optional;


@Component
public class SubscribeTicketsInfoQueryHandler implements CallbackQueryHandler {
    private static final CallbackQueryType HANDLER_QUERY_TYPE = CallbackQueryType.SUBSCRIBE;
    private final UserTicketsSubscriptionService subscriptionService;
    private final ParseQueryDataService parseService;
    private final ReplyMessagesService messagesService;
    private final UserDataCache userDataCache;
    private final TelegramBot telegramBot;


    public SubscribeTicketsInfoQueryHandler(UserTicketsSubscriptionService subscribeService,
                                            ParseQueryDataService parseService,
                                            ReplyMessagesService messagesService,
                                            UserDataCache userDataCache,
                                            @Lazy TelegramBot telegramBot) {
        this.subscriptionService = subscribeService;
        this.parseService = parseService;
        this.messagesService = messagesService;
        this.userDataCache = userDataCache;
        this.telegramBot = telegramBot;
    }


    @Override
    public CallbackQueryType getHandlerQueryType() {
        return HANDLER_QUERY_TYPE;
    }

    @Override
    public SendMessage handleCallbackQuery(CallbackQuery callbackQuery) {
        final long chatId = callbackQuery.getMessage().getChatId();
        final String trainNumber = parseService.parseTrainNumberFromSubscribeQuery(callbackQuery);
        final String dateDepart = parseService.parseDateDepartFromSubscribeQuery(callbackQuery);

        Optional<UserTicketsSubscription> userSubscriptionOptional = parseQueryData(callbackQuery);
        if (userSubscriptionOptional.isEmpty()) {
            return messagesService.getWarningReplyMessage(chatId, "reply.query.searchAgain");
        }

        UserTicketsSubscription userSubscription = userSubscriptionOptional.get();
        if (subscriptionService.hasTicketsSubscription(userSubscription)) {
            return messagesService.getWarningReplyMessage(chatId, "reply.query.train.userHasSubscription");
        }

        subscriptionService.saveUserSubscription(userSubscription);

        telegramBot.sendChangedInlineButtonText(callbackQuery,
                String.format("%s %s", Emojis.SUCCESS_SUBSCRIBED, UserChatButtonStatus.SUBSCRIBED), CallbackQueryType.QUERY_PROCESSED.name());

        return messagesService.getReplyMessage(chatId, "reply.query.train.subscribed", trainNumber, dateDepart);

    }


    private Optional<UserTicketsSubscription> parseQueryData(CallbackQuery usersQuery) {
        List<Train> foundedTrains = userDataCache.getSearchFoundedTrains(usersQuery.getMessage().getChatId());
        final long chatId = usersQuery.getMessage().getChatId();

        final String trainNumber = parseService.parseTrainNumberFromSubscribeQuery(usersQuery);
        final String dateDepart = parseService.parseDateDepartFromSubscribeQuery(usersQuery);

        Optional<Train> queriedTrainOptional = foundedTrains.stream().
                filter(train -> train.getNumber().equals(trainNumber) && train.getDateDepart().equals(dateDepart)).
                findFirst();

        if (queriedTrainOptional.isEmpty()) {
            return Optional.empty();
        }

        Train queriedTrain = queriedTrainOptional.get();
        final String trainName = queriedTrain.getBrand();
        final String stationDepart = queriedTrain.getStationDepart();
        final String stationArrival = queriedTrain.getStationArrival();
        final String dateArrival = queriedTrain.getDateArrival();
        final String timeDepart = queriedTrain.getTimeDepart();
        final String timeArrival = queriedTrain.getTimeArrival();
        final List<RailwayCarriage> availableCars = queriedTrain.getAvailableCarriages();

        return Optional.of(new UserTicketsSubscription(chatId, trainNumber, trainName, stationDepart, stationArrival, dateDepart, dateArrival, timeDepart, timeArrival, availableCars));
    }


}
