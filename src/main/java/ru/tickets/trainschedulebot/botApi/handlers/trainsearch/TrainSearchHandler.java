package ru.tickets.trainschedulebot.botApi.handlers.trainsearch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.tickets.trainschedulebot.botApi.BotState;
import ru.tickets.trainschedulebot.botApi.handlers.InputMessageHandler;
import ru.tickets.trainschedulebot.cache.UserDataCache;
import ru.tickets.trainschedulebot.model.Train;
import ru.tickets.trainschedulebot.service.ReplyMessagesService;
import ru.tickets.trainschedulebot.service.SendTicketsInfoService;
import ru.tickets.trainschedulebot.service.StationCodeService;
import ru.tickets.trainschedulebot.service.TrainTicketsGetInfoService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class TrainSearchHandler implements InputMessageHandler {
    private final UserDataCache userDataCache;
    private final TrainTicketsGetInfoService trainTicketsService;
    private final StationCodeService stationCodeService;
    private final SendTicketsInfoService sendTicketsInfoService;
    private final ReplyMessagesService messagesService;

    public TrainSearchHandler(UserDataCache userDataCache, TrainTicketsGetInfoService trainTicketsService,
                              StationCodeService stationCodeService, ReplyMessagesService messagesService,
                              SendTicketsInfoService sendTicketsInfoService) {
        this.userDataCache = userDataCache;
        this.trainTicketsService = trainTicketsService;
        this.stationCodeService = stationCodeService;
        this.sendTicketsInfoService = sendTicketsInfoService;
        this.messagesService = messagesService;
    }

    @Override
    public SendMessage handle(Message message) {
        if (userDataCache.getUsersCurrentBotState(Math.toIntExact(message.getFrom().getId())).equals(BotState.TRAINS_SEARCH)) {
            userDataCache.setUsersCurrentBotState(Math.toIntExact(message.getFrom().getId()), BotState.ASK_STATION_DEPART);
        }
        return processUsersInput(message);
    }

    @Override
    public BotState getHandlerName() {
        return BotState.TRAINS_SEARCH;
    }

    private SendMessage processUsersInput(Message inputMsg) {
        String usersAnswer = inputMsg.getText();
        int userId = Math.toIntExact(inputMsg.getFrom().getId());
        long chatId = inputMsg.getChatId();
        SendMessage replyToUser = messagesService.getWarningReplyMessage(chatId, "reply.trainSearch.tryAgain");
        TrainSearchRequestData requestData = userDataCache.getUserTrainSearchData(userId);

        BotState botState = userDataCache.getUsersCurrentBotState(userId);
        if (botState.equals(BotState.ASK_STATION_DEPART)) {
            replyToUser = messagesService.getReplyMessage(chatId, "reply.trainSearch.enterStationDepart");
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_STATION_ARRIVAL);
        }

        if (botState.equals(BotState.ASK_STATION_ARRIVAL)) {

            int departureStationCode = stationCodeService.getStationCode(usersAnswer);
            if (departureStationCode == -1) {
                return messagesService.getWarningReplyMessage(chatId, "reply.trainSearch.stationNotFound");
            }

            requestData.setDepartureStationCode(departureStationCode);
            replyToUser = messagesService.getReplyMessage(chatId, "reply.trainSearch.enterStationArrival");
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_DATE_DEPART);
        }

        if (botState.equals(BotState.ASK_DATE_DEPART)) {
            int arrivalStationCode = stationCodeService.getStationCode(usersAnswer);
            if (arrivalStationCode == -1) {
                return messagesService.getWarningReplyMessage(chatId, "reply.trainSearch.stationNotFound");
            }

            if (arrivalStationCode == requestData.getDepartureStationCode()) {
                return messagesService.getWarningReplyMessage(chatId, "reply.trainSearch.stationsEquals");
            }

            requestData.setArrivalStationCode(arrivalStationCode);
            replyToUser = messagesService.getReplyMessage(chatId, "reply.trainSearch.enterDateDepart");
            userDataCache.setUsersCurrentBotState(userId, BotState.DATE_DEPART_RECEIVED);
        }

        if (botState.equals(BotState.DATE_DEPART_RECEIVED)) {
            Date dateDepart;
            try {
                dateDepart = new SimpleDateFormat("dd.MM.yyyy").parse(usersAnswer);
            } catch (ParseException e) {
                return messagesService.getWarningReplyMessage(chatId, "reply.trainSearch.wrongTimeFormat");
            }
            requestData.setDateDepart(dateDepart);

            List<Train> trainList = trainTicketsService.getTrainTicketsList(chatId, requestData.getDepartureStationCode(),
                    requestData.getArrivalStationCode(), dateDepart);
            if (trainList.isEmpty()) {
                return messagesService.getReplyMessage(chatId, "reply.trainSearch.trainsNotFound");
            }

            sendTicketsInfoService.sendTrainTicketsInfo(chatId, trainList);
            userDataCache.setUsersCurrentBotState(userId, BotState.SHOW_MAIN_MENU);
            replyToUser = messagesService.getSuccessReplyMessage(chatId, "reply.trainSearch.finishedOK");

        }
        userDataCache.saveTrainSearchData(userId, requestData);
        return replyToUser;
    }

}