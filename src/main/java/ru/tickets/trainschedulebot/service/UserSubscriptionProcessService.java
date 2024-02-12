package ru.tickets.trainschedulebot.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.tickets.trainschedulebot.botApi.TelegramBot;
import ru.tickets.trainschedulebot.model.RailwayCarriage;
import ru.tickets.trainschedulebot.model.Train;
import ru.tickets.trainschedulebot.model.UserTicketsSubscription;
import ru.tickets.trainschedulebot.utils.Emojis;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
public class UserSubscriptionProcessService {

    private static final Logger log = LoggerFactory.getLogger(UserSubscriptionProcessService.class);
    private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");
    private UserTicketsSubscriptionService subscriptionService;
    private TrainTicketsGetInfoService trainTicketsGetInfoService;
    private StationCodeService stationCodeService;
    private CarriagesProcessingService carriagesProcessingService;
    private ReplyMessagesService messagesService;
    private TelegramBot telegramBot;

    public UserSubscriptionProcessService(UserTicketsSubscriptionService subscriptionService,
                                          TrainTicketsGetInfoService trainTicketsGetInfoService,
                                          StationCodeService stationCodeService,
                                          CarriagesProcessingService carriagesProcessingService,
                                          ReplyMessagesService messagesService,
                                          @Lazy TelegramBot telegramBot) {
        this.subscriptionService = subscriptionService;
        this.trainTicketsGetInfoService = trainTicketsGetInfoService;
        this.stationCodeService = stationCodeService;
        this.carriagesProcessingService = carriagesProcessingService;
        this.messagesService = messagesService;
        this.telegramBot = telegramBot;
    }
    @Scheduled(fixedRateString = "${subscriptions.processPeriod}")
    public void processAllUsersSubscriptions() {
        try {
            log.info("Выполняю обработку подписок пользователей.");
            subscriptionService.getAllSubscriptions().forEach(this::processSubscription);
            log.info("Завершил обработку подписок пользователей.");
        } catch (NullPointerException e) {
            log.warn("Subscription is null: " + e.getMessage());
        }


    }

    private void processSubscription(UserTicketsSubscription subscription) {
        List<Train> actualTrains = getActualTrains(subscription);

        if (isTrainHasDeparted(actualTrains, subscription)) {
            subscriptionService.deleteUserSubscription(subscription.getId());
            telegramBot.sendMessage(messagesService.getReplyMessage(subscription.getChatId(), "subscription.trainHasDeparted",
                    Emojis.NOTIFICATION_BELL, subscription.getTrainNumber(), subscription.getTrainName(),
                    subscription.getDateDepart(), subscription.getTimeDepart()));
            return;
        }

        actualTrains.forEach(actualTrain -> {

            if (actualTrain.getNumber().equals(subscription.getTrainNumber()) &&
                    actualTrain.getDateDepart().equals(subscription.getDateDepart())) {

                List<RailwayCarriage> actualCarsWithMinimumPrice = carriagesProcessingService.filterCarriagesWithMinPrice(actualTrain.getAvailableCarriages());

                Map<String, List<RailwayCarriage>> updatedCarsNotification = processCarriagesLists(subscription.getSubscribedCars(),
                        actualCarsWithMinimumPrice);

                if (!updatedCarsNotification.isEmpty()) {
                    String priceChangesMessage = updatedCarsNotification.keySet().iterator().next();
                    List<RailwayCarriage> updatedCars = updatedCarsNotification.get(priceChangesMessage);

                    subscription.setSubscribedCars(updatedCars);
                    subscriptionService.saveUserSubscription(subscription);
                    sendUserNotification(subscription, priceChangesMessage, updatedCars);
                }
            }
        });
    }

    private List<Train> getActualTrains(UserTicketsSubscription subscription) {
        int stationDepartCode = stationCodeService.getStationCode(subscription.getStationDepart());
        int stationArrivalCode = stationCodeService.getStationCode(subscription.getStationArrival());
        Date dateDeparture = parseDateDeparture(subscription.getDateDepart());

        return trainTicketsGetInfoService.getTrainTicketsList(subscription.getChatId(),
                stationDepartCode, stationArrivalCode, dateDeparture);
    }

    private boolean isTrainHasDeparted(List<Train> actualTrains, UserTicketsSubscription subscription) {
        return actualTrains.stream().map(Train::getNumber).noneMatch(Predicate.isEqual(subscription.getTrainNumber()));
    }

    private Map<String, List<RailwayCarriage>> processCarriagesLists(List<RailwayCarriage> subscribedCars, List<RailwayCarriage> actualCars) {
        StringBuilder notificationMessage = new StringBuilder();

        for (RailwayCarriage subscribedCar : subscribedCars) {

            for (RailwayCarriage actualCar : actualCars) {
                if (actualCar.getCarType().equals(subscribedCar.getCarType())) {

                    if (actualCar.getMinimalPrice() > subscribedCar.getMinimalPrice()) {
                        notificationMessage.append(messagesService.getReplyText("subscription.PriceUp", Emojis.NOTIFICATION_PRICE_UP,
                                actualCar.getCarType(), subscribedCar.getMinimalPrice(), actualCar.getMinimalPrice()));
                        subscribedCar.setMinimalPrice(actualCar.getMinimalPrice());
                    } else if (actualCar.getMinimalPrice() < subscribedCar.getMinimalPrice()) {
                        notificationMessage.append(messagesService.getReplyText("subscription.PriceDown", Emojis.NOTIFICATION_PRICE_DOWN,
                                actualCar.getCarType(), subscribedCar.getMinimalPrice(), actualCar.getMinimalPrice()));
                        subscribedCar.setMinimalPrice(actualCar.getMinimalPrice());
                    }
                    subscribedCar.setFreeSeats(actualCar.getFreeSeats());
                }
            }
        }

        return notificationMessage.isEmpty() ? Collections.emptyMap() : Collections.singletonMap(notificationMessage.toString(), subscribedCars);
    }

    private void sendUserNotification(UserTicketsSubscription subscription, String priceChangeMessage, List<RailwayCarriage> updatedCarriages) {
        StringBuilder notificationMessage = new StringBuilder(messagesService.getReplyText("subscription.trainTicketsPriceChanges",
                Emojis.NOTIFICATION_BELL, subscription.getTrainNumber(), subscription.getTrainName(),
                subscription.getDateDepart(), subscription.getTimeDepart(), subscription.getStationArrival())).append(priceChangeMessage);

        notificationMessage.append(messagesService.getReplyText("subscription.lastTicketPrices"));

        for (RailwayCarriage carriage : updatedCarriages) {
            notificationMessage.append(messagesService.getReplyText("subscription.carsTicketsInfo",
                    carriage.getCarType(), carriage.getFreeSeats(), carriage.getMinimalPrice()));
        }

        telegramBot.sendMessage(subscription.getChatId(), notificationMessage.toString());
    }

    private Date parseDateDeparture(String dateDeparture) {
        Date dateDepart = null;
        try {
            dateDepart = DATE_FORMAT.parse(dateDeparture);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateDepart;
    }
}
