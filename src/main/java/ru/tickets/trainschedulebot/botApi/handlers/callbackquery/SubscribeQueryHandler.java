package ru.tickets.trainschedulebot.botApi.handlers.callbackquery;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.tickets.trainschedulebot.botApi.TelegramBot;
import ru.tickets.trainschedulebot.cache.UserDataCache;
import ru.tickets.trainschedulebot.model.Car;
import ru.tickets.trainschedulebot.model.Train;
import ru.tickets.trainschedulebot.model.UserTicketsSubscription;
import ru.tickets.trainschedulebot.service.ParseQueryDataService;
import ru.tickets.trainschedulebot.service.ReplyMessagesService;
import ru.tickets.trainschedulebot.service.SubscriptionService;

import java.util.List;
import java.util.Optional;

@Component
public class SubscribeQueryHandler implements CallbackQueryHandler {
    private static final CallbackQueryType HANDLER_QUERY_TYPE = CallbackQueryType.SUBSCRIBE;
    private final SubscriptionService subscriptionService;
    private final ParseQueryDataService parseService;
    private final ReplyMessagesService messagesService;
    private final UserDataCache userDataCache;
    private final TelegramBot telegramBot;

    public SubscribeQueryHandler(SubscriptionService subscribeService,
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

        System.out.println(userSubscriptionOptional.get());


        UserTicketsSubscription userSubscription = userSubscriptionOptional.get();
        if (subscriptionService.hasTicketsSubscription(userSubscription)) {
            return messagesService.getWarningReplyMessage(chatId, "reply.query.train.userHasSubscription");
        }

        subscriptionService.saveUserSubscription(userSubscription);

        System.out.println("SUbs service callbackquery = " + callbackQuery.getData());

        String newCallbackData = subscriptionService.getByTrainNumberAndDateDepart(trainNumber, dateDepart);
        String callbackData = String.format("%s|%s", CallbackQueryType.UNSUBSCRIBE.name(), newCallbackData);

        telegramBot.updateAndSendInlineKeyBoardMessage(callbackQuery,
                 String.format("%s", UserButtonStatus.SUBSCRIBE), callbackData);


        return messagesService.getReplyMessage(chatId, "reply.query.train.subscribed", trainNumber, dateDepart);

    }


    private Optional<UserTicketsSubscription> parseQueryData(CallbackQuery usersQuery) {
        List<Train> foundedTrains = userDataCache.getSearchFoundedTrains(usersQuery.getMessage().getChatId());
        final long chatId = usersQuery.getMessage().getChatId();

        System.out.println("User Query:" + usersQuery.getData());
        System.out.println("train size:" + foundedTrains.size());

        final String trainNumber = parseService.parseTrainNumberFromSubscribeQuery(usersQuery);
        final String dateDepart = parseService.parseDateDepartFromSubscribeQuery(usersQuery);

        System.out.println("Train number:" + trainNumber);
        System.out.println("Date depart:" + dateDepart);
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
        final List<Car> availableCars = queriedTrain.getAvailableCars();

        return Optional.of(new UserTicketsSubscription(chatId, trainNumber, trainName, stationDepart, stationArrival, dateDepart, dateArrival, timeDepart, timeArrival, availableCars));
    }


}
