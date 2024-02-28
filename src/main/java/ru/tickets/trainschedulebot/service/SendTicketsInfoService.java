package ru.tickets.trainschedulebot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.tickets.trainschedulebot.botApi.handlers.callbackquery.CallbackQueryType;
import ru.tickets.trainschedulebot.botApi.handlers.state.UserButtonStatus;
import ru.tickets.trainschedulebot.cache.UserDataCache;
import ru.tickets.trainschedulebot.model.Car;
import ru.tickets.trainschedulebot.model.Train;
import ru.tickets.trainschedulebot.utils.Emojis;

import java.util.List;

/**
 * Service class for sending train tickets information to Telegram users.
 * This class interacts with other services to process and send train-related messages.
 *
 * @author Elgun Dilanchiev
 * @version 1.0
 * @since 2024-02-28
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SendTicketsInfoService {

    /**
     * The service for sending messages.
     */
    private final SendMessageService sendMessageService;

    /**
     * The service for processing train carriages.
     */
    private final CarsProcessingService carsProcessingService;

    /**
     * The service for managing user subscriptions.
     */
    private final SubscriptionService subscriptionService;

    /**
     * The service for sending reply messages.
     */
    private final ReplyMessagesService messagesService;

    /**
     * The cache for storing user data.
     */
    private final UserDataCache userDataCache;

    /**
     * Sends train tickets information to the specified chat ID for a list of trains.
     *
     * @param chatId       The ID of the chat to which the message will be sent.
     * @param trainsList   The list of trains for which to send tickets information.
     */
    public void sendTrainTicketsInfo(long chatId, List<Train> trainsList) {
        for (Train train : trainsList) {
            sendTrainInfoMessage(chatId, train);
        }
        userDataCache.saveSearchFoundedTrains(chatId, trainsList);
    }

    /**
     * Sends information about a specific train to the specified chat ID.
     *
     * @param chatId The ID of the chat to which the message will be sent.
     * @param train  The train for which to send information.
     */
    private void sendTrainInfoMessage(long chatId, Train train) {
        StringBuilder carsInfo = buildCarsInfo(train);

        String buttonText;
        String trainsInfoData;

        if (subscriptionService.isUserSubscribed(train.getNumber(), train.getDateDepart())) {
            buttonText = UserButtonStatus.SUBSCRIBED.toString();
            trainsInfoData = buildUnsubscribeCallbackData(train);
        } else {
            buttonText = UserButtonStatus.UNSUBSCRIBED.toString();
            trainsInfoData = buildSubscribeCallbackData(train);
        }

        String trainTicketsInfoMessage = buildTrainTicketsInfoMessage(train, carsInfo);

        sendMessageService.sendInlineKeyBoardMessage(chatId, trainTicketsInfoMessage, buttonText, trainsInfoData);
    }

    /**
     * Builds information about the train carriages, including type, free seats, and minimal price.
     *
     * @param train The train for which to build carriage information.
     * @return A StringBuilder containing information about the train carriages.
     */
    private StringBuilder buildCarsInfo(Train train) {
        List<Car> carsWithMinPrice = carsProcessingService.filterCarriagesWithMinPrice(train.getAvailableCars());
        train.setAvailableCars(carsWithMinPrice);
        StringBuilder carsInfo = new StringBuilder();

        for (Car car : carsWithMinPrice) {
            carsInfo.append(messagesService.getReplyText("subscription.carsTicketsInfo", Emojis.BED,
                    car.getCarType(), Emojis.MINUS, car.getFreeSeats(), Emojis.MINUS, car.getMinimalPrice()));
        }
        return carsInfo;
    }

    /**
     * Builds the callback data for subscribing to a train.
     *
     * @param train The train for which to build the subscribe callback data.
     * @return The subscribe callback data.
     */
    private String buildSubscribeCallbackData(Train train) {
        return String.format("%s|%s|%s", CallbackQueryType.SUBSCRIBE, train.getNumber(), train.getDateDepart());
    }

    /**
     * Builds the callback data for unsubscribing from a train.
     *
     * @param train The train for which to build the unsubscribe callback data.
     * @return The unsubscribe callback data.
     */
    private String buildUnsubscribeCallbackData(Train train) {
        String unsubscribeCallbackData = subscriptionService.getSubscriptionIdByTrainNumberAndDateDepart(train.getNumber(), train.getDateDepart());
        return String.format("%s|%s", CallbackQueryType.UNSUBSCRIBE, unsubscribeCallbackData);
    }

    /**
     * Builds the message containing information about a train, including its schedule, available carriages, and more.
     *
     * @param train    The train for which to build the information message.
     * @param carsInfo The information about the available carriages.
     * @return The message containing information about the train.
     */
    private String buildTrainTicketsInfoMessage(Train train, StringBuilder carsInfo) {
        String[] parts = train.getTimeInWay().split(":");
        String hours = parts[0];
        String minutes = parts[1];

        return messagesService.getReplyText("reply.trainSearch.trainInfo",
                Emojis.TRAIN, train.getNumber(), train.getBrand(), train.getStationDepart(), train.getDateDepart(), train.getTimeDepart(),
                train.getStationArrival(), train.getDateArrival(), train.getTimeArrival(),
                hours, minutes, carsInfo);
    }
}