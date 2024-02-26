package ru.tickets.trainschedulebot.botApi.handlers.trainsearch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.tickets.trainschedulebot.botApi.handlers.state.BotState;
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
@RequiredArgsConstructor
public class TrainSearchHandler implements InputMessageHandler {
    private final UserDataCache userDataCache;
    private final TrainTicketsGetInfoService trainTicketsService;
    private final StationCodeService stationCodeService;
    private final SendTicketsInfoService sendTicketsInfoService;
    private final ReplyMessagesService messagesService;

    @Override
    public SendMessage handle(Message message) {
        handleInputMessage(message);
        return processUsersInput(message);
    }

    @Override
    public BotState getHandlerName() {
        return BotState.TRAINS_SEARCH;
    }


    private void handleInputMessage(Message message) {
        if (userDataCache.getUsersCurrentBotState(Math.toIntExact(message.getFrom().getId())).equals(BotState.TRAINS_SEARCH)) {
            userDataCache.setUsersCurrentBotState(Math.toIntExact(message.getFrom().getId()), BotState.ASK_STATION_DEPART);
        }
    }
    private SendMessage processUsersInput(Message inputMsg) {
        String inputMessageText = inputMsg.getText();
        long userId = inputMsg.getFrom().getId();
        long chatId = inputMsg.getChatId();
        SendMessage replyToUser = messagesService.getWarningReplyMessage(chatId, "reply.trainSearch.tryAgain");
        TrainSearchRequestData requestData = userDataCache.getUserTrainSearchData(userId);

        BotState botState = userDataCache.getUsersCurrentBotState(userId);

        switch (botState) {
            case ASK_STATION_DEPART:
                replyToUser = processAskStationDepart(userId, chatId);
                break;
            case ASK_STATION_ARRIVAL:
                replyToUser = processAskStationArrival(userId, chatId, inputMessageText, requestData);
                break;
            case ASK_DATE_DEPART:
                replyToUser = processAskDateDepart(userId, chatId, inputMessageText, requestData);
                break;
            case DATE_DEPART_RECEIVED:
                replyToUser = processDateDepartReceived(userId, chatId, inputMessageText, requestData);
                break;
            default:
                return messagesService.getWarningReplyMessage(chatId, "reply.query.failed");
        }

        userDataCache.saveTrainSearchData(userId, requestData);
        return replyToUser;
    }

    private SendMessage processAskStationDepart(long userId, long chatId) {
        userDataCache.setUsersCurrentBotState(userId, BotState.ASK_STATION_ARRIVAL);
        return messagesService.getReplyMessage(chatId, "reply.trainSearch.enterStationDepart");
    }

    private SendMessage processAskStationArrival(long userId, long chatId, String inputMessageText, TrainSearchRequestData requestData) {
        int departureStationCode = stationCodeService.getStationCode(inputMessageText);
        if (departureStationCode == -1) {
            return messagesService.getWarningReplyMessage(chatId, "reply.trainSearch.stationNotFound");
        }

        requestData.setDepartureStationCode(departureStationCode);
        userDataCache.setUsersCurrentBotState(userId, BotState.ASK_DATE_DEPART);
        return messagesService.getReplyMessage(chatId, "reply.trainSearch.enterStationArrival");
    }

    private SendMessage processAskDateDepart(long userId, long chatId, String inputMessageText, TrainSearchRequestData requestData) {
        int arrivalStationCode = stationCodeService.getStationCode(inputMessageText);
        if (arrivalStationCode == -1) {
            return messagesService.getWarningReplyMessage(chatId, "reply.trainSearch.stationNotFound");
        }

        if (arrivalStationCode == requestData.getDepartureStationCode()) {
            return messagesService.getWarningReplyMessage(chatId, "reply.trainSearch.stationsEquals");
        }

        requestData.setArrivalStationCode(arrivalStationCode);
        userDataCache.setUsersCurrentBotState(userId, BotState.DATE_DEPART_RECEIVED);
        return messagesService.getReplyMessage(chatId, "reply.trainSearch.enterDateDepart");
    }

    private SendMessage processDateDepartReceived(long userId, long chatId, String inputMessageText, TrainSearchRequestData requestData) {
        Date dateDepart;
        try {
            dateDepart = new SimpleDateFormat("dd.MM.yyyy").parse(inputMessageText);
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
        return messagesService.getSuccessReplyMessage(chatId, "reply.trainSearch.finishedOK");
    }
}