package ru.tickets.trainschedulebot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ru.tickets.trainschedulebot.botApi.TelegramBot;
import ru.tickets.trainschedulebot.botApi.handlers.callbackquery.CallbackQueryType;
import ru.tickets.trainschedulebot.cache.UserDataCache;
import ru.tickets.trainschedulebot.model.RailwayCarriage;
import ru.tickets.trainschedulebot.model.Train;
import ru.tickets.trainschedulebot.utils.Emojis;

import java.util.List;

/**
 * @author Elgun Dilanchiev
 */
@Service
public class SendTicketsInfoService {

    @Lazy
    private final TelegramBot telegramBot;
    private CarriagesProcessingService carriagesProcessingService;
    private ReplyMessagesService messagesService;
    private UserDataCache userDataCache;

    public SendTicketsInfoService(CarriagesProcessingService carsProcessingService,
                                  UserDataCache userDataCache,
                                  ReplyMessagesService messagesService,
                                  @Lazy TelegramBot telegramBot) {
        this.carriagesProcessingService = carsProcessingService;
        this.userDataCache = userDataCache;
        this.messagesService = messagesService;
        this.telegramBot = telegramBot;
    }

    public void sendTrainTicketsInfo(long chatId, List<Train> trainsList) {
        for (Train train : trainsList) {
            StringBuilder carsInfo = new StringBuilder();
            List<RailwayCarriage> carriagesWithMinPrice = carriagesProcessingService.filterCarriagesWithMinPrice(train.getAvailableCarriages());
            train.setAvailableCarriages(carriagesWithMinPrice);

            for (RailwayCarriage carriages : carriagesWithMinPrice) {
                carsInfo.append(messagesService.getReplyText("subscription.carsTicketsInfo",
                        carriages.getCarType(), carriages.getFreeSeats(), carriages.getMinimalPrice()));
            }

            String trainTicketsInfoMessage = messagesService.getReplyText("reply.trainSearch.trainInfo",
                    Emojis.TRAIN, train.getNumber(), train.getBrand(), train.getStationDepart(), train.getDateDepart(), train.getTimeDepart(),
                    train.getStationArrival(), train.getDateArrival(), train.getTimeArrival(),
                    Emojis.TIME_IN_WAY, train.getTimeInWay(), carsInfo);

            String trainsInfoData = String.format("%s|%s|%s", CallbackQueryType.SUBSCRIBE,
                    train.getNumber(), train.getDateDepart());

            telegramBot.sendInlineKeyBoardMessage(chatId, trainTicketsInfoMessage, "Подписаться", trainsInfoData);
        }
        userDataCache.saveSearchFoundedTrains(chatId, trainsList);
    }



}
