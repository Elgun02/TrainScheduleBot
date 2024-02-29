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

/**
 * Handler for processing train search-related user input.
 * This handler manages the train search conversation flow, handling user input at different stages
 * such as station departure, station arrival, and date of departure.
 * It uses a UserDataCache to manage user-specific data during the conversation flow.
 *
 * @author Elgun Dilanchiev
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TrainSearchHandler implements InputMessageHandler {
    private final UserDataCache userDataCache;
    private final TrainTicketsGetInfoService trainTicketsService;
    private final StationCodeService stationCodeService;
    private final SendTicketsInfoService sendTicketsInfoService;
    private final ReplyMessagesService messagesService;

    /**
     * Handles the user's input message and processes it based on the current bot state.
     *
     * @param message The input message from the user.
     * @return The reply message to be sent back to the user.
     */
    @Override
    public SendMessage handle(Message message) {
        handleInputMessage(message);
        return processUsersInput(message);
    }


    /**
     * Gets the corresponding bot state for this handler.
     *
     * @return The bot state associated with this handler.
     */
    @Override
    public BotState getHandlerName() {
        return BotState.TRAINS_SEARCH;
    }


    /**
     * Handles the initial user input message, setting the bot state to ask for the departure station.
     *
     * @param message The incoming message from the user.
     */
    private void handleInputMessage(Message message) {
        if (userDataCache.getUsersCurrentBotState(Math.toIntExact(message.getFrom().getId())).equals(BotState.TRAINS_SEARCH)) {
            userDataCache.setUsersCurrentBotState(Math.toIntExact(message.getFrom().getId()), BotState.ASK_STATION_DEPART);
        }
    }

    /**
     * Processes the user's input message during the conversation flow.
     *
     * @param inputMsg The incoming message from the user.
     * @return The reply message to be sent back to the user.
     */
    private SendMessage processUsersInput(Message inputMsg) {
        String inputMessageText = inputMsg.getText();
        long userId = inputMsg.getFrom().getId();
        long chatId = inputMsg.getChatId();
        SendMessage replyToUser;
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

    /**
     * Processes the user's input during the "Ask Station Depart" state.
     *
     * @param userId The ID of the user.
     * @param chatId The ID of the chat.
     * @return The reply message to be sent back to the user.
     */
    private SendMessage processAskStationDepart(long userId, long chatId) {
        userDataCache.setUsersCurrentBotState(userId, BotState.ASK_STATION_ARRIVAL);
        return messagesService.getReplyMessage(chatId, "reply.trainSearch.enterStationDepart");
    }

    /**
     * Processes the user input for asking the arrival station.
     *
     * @param userId The user's ID.
     * @param chatId The chat ID.
     * @param inputMessageText The user's input message.
     * @param requestData The train search request data.
     * @return The reply message to be sent back to the user.
     */
    private SendMessage processAskStationArrival(long userId, long chatId, String inputMessageText, TrainSearchRequestData requestData) {
        int departureStationCode = stationCodeService.getStationCode(inputMessageText);
        if (departureStationCode == -1) {
            return messagesService.getWarningReplyMessage(chatId, "reply.trainSearch.stationNotFound");
        }

        requestData.setDepartureStationCode(departureStationCode);
        userDataCache.setUsersCurrentBotState(userId, BotState.ASK_DATE_DEPART);
        return messagesService.getReplyMessage(chatId, "reply.trainSearch.enterStationArrival");
    }

    /**
     * Processes the user input for asking the date of departure.
     *
     * @param userId The user's ID.
     * @param chatId The chat ID.
     * @param inputMessageText The user's input message.
     * @param requestData The train search request data.
     * @return The reply message to be sent back to the user.
     */
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


    /**
     * Processes the user's input during the "Date Depart Received" state.
     *
     * @param userId The ID of the user.
     * @param chatId The ID of the chat.
     * @param inputMessageText The text of the user's input.
     * @param requestData The train search request data.
     * @return The reply message to be sent back to the user.
     */
    private SendMessage processDateDepartReceived(long userId, long chatId, String inputMessageText, TrainSearchRequestData requestData) {
        Date dateDepart;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            dateFormat.setLenient(false);
            dateDepart = dateFormat.parse(inputMessageText);
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