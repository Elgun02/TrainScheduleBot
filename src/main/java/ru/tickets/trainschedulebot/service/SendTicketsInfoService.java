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
            sendTrainInfoMessage(chatId, train);
        }
        userDataCache.saveSearchFoundedTrains(chatId, trainsList);
    }

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

    private String buildSubscribeCallbackData(Train train) {
        return String.format("%s|%s|%s", CallbackQueryType.SUBSCRIBE, train.getNumber(), train.getDateDepart());
    }

    private String buildUnsubscribeCallbackData(Train train) {
        String unsubscribeCallbackData = subscriptionService.getSubscriptionIdByTrainNumberAndDateDepart(train.getNumber(), train.getDateDepart());
        return String.format("%s|%s", CallbackQueryType.UNSUBSCRIBE, unsubscribeCallbackData);
    }


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