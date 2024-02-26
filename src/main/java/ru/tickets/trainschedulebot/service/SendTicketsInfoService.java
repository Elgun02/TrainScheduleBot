package ru.tickets.trainschedulebot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ru.tickets.trainschedulebot.botApi.TelegramBot;
import ru.tickets.trainschedulebot.botApi.handlers.callbackquery.CallbackQueryType;
import ru.tickets.trainschedulebot.botApi.handlers.callbackquery.UserButtonStatus;
import ru.tickets.trainschedulebot.cache.UserDataCache;
import ru.tickets.trainschedulebot.model.Car;
import ru.tickets.trainschedulebot.model.Train;
import ru.tickets.trainschedulebot.utils.Emojis;

import java.util.List;

/**
 * @author Elgun Dilanchiev
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SendTicketsInfoService {
    private final SendMessageService sendMessageService;
    private final CarsProcessingService carsProcessingService;
    private final SubscriptionService subscriptionService;
    private final ReplyMessagesService messagesService;
    private final UserDataCache userDataCache;

    public void sendTrainTicketsInfo(long chatId, List<Train> trainsList) {
        for (Train train : trainsList) {
            StringBuilder carsInfo = new StringBuilder();
            List<Car> carsWithMinPrice = carsProcessingService.filterCarriagesWithMinPrice(train.getAvailableCars());
            train.setAvailableCars(carsWithMinPrice);

            for (Car car : carsWithMinPrice) {
                carsInfo.append(messagesService.getReplyText("subscription.carsTicketsInfo", Emojis.BED,
                        car.getCarType(), Emojis.MINUS, car.getFreeSeats(), Emojis.MINUS, car.getMinimalPrice()));
            }

            String[] parts = train.getTimeInWay().split(":");
            String hours = parts[0];
            String minutes = parts[1];

            String buttonText;
            String trainsInfoData;

            if (subscriptionService.isUserSubscribed(train.getNumber(), train.getDateDepart())) {
                String unsubscribeCallbackData = subscriptionService.getSubscriptionIdByTrainNumberAndDateDepart(train.getNumber(), train.getDateDepart());
                buttonText = UserButtonStatus.SUBSCRIBED.toString();
                trainsInfoData = String.format("%s|%s", CallbackQueryType.UNSUBSCRIBE,
                        unsubscribeCallbackData);
            } else {
                buttonText = UserButtonStatus.UNSUBSCRIBED.toString();
                trainsInfoData = String.format("%s|%s|%s", CallbackQueryType.SUBSCRIBE,
                        train.getNumber(), train.getDateDepart());
                System.out.println("else: " + trainsInfoData);
            }

            String trainTicketsInfoMessage = messagesService.getReplyText("reply.trainSearch.trainInfo",
                    Emojis.TRAIN, train.getNumber(), train.getBrand(), train.getStationDepart(), train.getDateDepart(), train.getTimeDepart(),
                    train.getStationArrival(), train.getDateArrival(), train.getTimeArrival(),
                    hours, minutes, carsInfo);

            System.out.println("TRAIn INFO DATA = " + trainsInfoData);

            sendMessageService.sendInlineKeyBoardMessage(chatId, trainTicketsInfoMessage, buttonText, trainsInfoData);
        }
        userDataCache.saveSearchFoundedTrains(chatId, trainsList);
    }
}
