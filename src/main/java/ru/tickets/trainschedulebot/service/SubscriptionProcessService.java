package ru.tickets.trainschedulebot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.tickets.trainschedulebot.model.Car;
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

/**
 * Service class for processing user subscriptions and notifying users about train ticket updates.
 * This class schedules periodic tasks to check and process subscriptions, handling train departures and notifying users about ticket price changes.
 *
 * @author Elgun Dilanchiev
 * @version 1.0
 * @since 2024-02-28
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionProcessService {

    /**
     * The date format for parsing date strings.
     */
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");

    /**
     * Service for retrieving train ticket information.
     */
    private final TrainTicketsGetInfoService trainTicketsGetInfoService;

    /**
     * Service for processing train carriages information.
     */
    private final CarsProcessingService carsProcessingService;

    /**
     * Service for managing user subscriptions.
     */
    private final SubscriptionService subscriptionService;

    /**
     * Service for retrieving station codes based on station names.
     */
    private final StationCodeService stationCodeService;

    /**
     * Service for generating reply messages.
     */
    private final ReplyMessagesService messagesService;

    /**
     * Service for sending Telegram messages.
     */
    private final SendMessageService sendMessageService;

    /**
     * Scheduled task to process all user subscriptions at fixed intervals.
     */
    @Scheduled(fixedRateString = "${subscriptions.processPeriod}")
    public void processAllUsersSubscriptions() {
        log.info("Started processing user subscriptions..");
        subscriptionService.getAllSubscriptions().forEach(this::processSubscription);
        log.info("Finished processing user subscriptions.");
    }

    /**
     * Processes a user subscription, checking for updates in train information and notifying users.
     *
     * @param subscription The user subscription to process.
     */
    private void processSubscription(UserTicketsSubscription subscription) {
        List<Train> actualTrains = getActualTrains(subscription);

        if (isTrainHasDeparted(actualTrains, subscription)) {
            handleDepartedTrain(subscription);
            return;
        }

        actualTrains.forEach(actualTrain -> processTrain(subscription, actualTrain));
    }

    /**
     * Retrieves the list of actual trains based on the user subscription.
     *
     * @param subscription The user subscription for which to retrieve actual trains.
     * @return A list of actual trains.
     */
    private List<Train> getActualTrains(UserTicketsSubscription subscription) {
        int stationDepartCode = stationCodeService.getStationCode(subscription.getStationDepart());
        int stationArrivalCode = stationCodeService.getStationCode(subscription.getStationArrival());
        Date dateDeparture = parseDateDeparture(subscription.getDateDepart());

        return trainTicketsGetInfoService.getTrainTicketsList(subscription.getChatId(),
                stationDepartCode, stationArrivalCode, dateDeparture);
    }

    /**
     * Checks if the subscribed train has departed and handles the case.
     *
     * @param actualTrains   The list of actual trains.
     * @param subscription   The user subscription.
     * @return True if the train has departed, false otherwise.
     */
    private boolean isTrainHasDeparted(List<Train> actualTrains, UserTicketsSubscription subscription) {
        return actualTrains.stream()
                .map(Train::getNumber)
                .noneMatch(Predicate.isEqual(subscription.getTrainNumber()));
    }

    /**
     * Handles the case when the subscribed train has departed.
     *
     * @param subscription The user subscription for the departed train.
     */
    private void handleDepartedTrain(UserTicketsSubscription subscription) {
        subscriptionService.deleteUserSubscriptionById(subscription.getId());
        sendMessageService.sendMessage(messagesService.getReplyMessage(subscription.getChatId(), "subscription.trainHasDeparted",
                Emojis.NOTIFICATION_BELL, subscription.getTrainNumber(), subscription.getTrainName(),
                subscription.getDateDepart(), subscription.getTimeDepart()));
    }

    /**
     * Processes an actual train, checking for updates in carriages information and notifying users.
     *
     * @param subscription The user subscription.
     * @param actualTrain  The actual train information.
     */
    private void processTrain(UserTicketsSubscription subscription, Train actualTrain) {
        if (isSameTrainAndDate(actualTrain, subscription)) {
            List<Car> actualCarsWithMinimumPrice = carsProcessingService.filterCarriagesWithMinPrice(actualTrain.getAvailableCars());
            Map<String, List<Car>> updatedCarsNotification = processCarsLists(subscription.getSubscribedCars(), actualCarsWithMinimumPrice);

            if (!updatedCarsNotification.isEmpty()) {
                updateSubscriptionAndNotifyUser(subscription, updatedCarsNotification);
            }
        }
    }

    /**
     * Updates the user subscription and notifies the user about the changes in subscribed cars.
     *
     * @param subscription           The user subscription.
     * @param updatedCarsNotification The map containing the notification message and updated cars list.
     */
    private void updateSubscriptionAndNotifyUser(UserTicketsSubscription subscription, Map<String, List<Car>> updatedCarsNotification) {
        String priceChangesMessage = updatedCarsNotification.keySet().iterator().next();
        List<Car> updatedCars = updatedCarsNotification.get(priceChangesMessage);

        subscription.setSubscribedCars(updatedCars);
        subscriptionService.saveUserSubscription(subscription);
        sendUserNotification(subscription, priceChangesMessage, updatedCars);
    }

    /**
     * Sends a notification message to the user about the changes in subscribed cars.
     *
     * @param subscription      The user subscription.
     * @param priceChangeMessage The message indicating the price changes.
     * @param updatedCars        The list of updated cars.
     */
    private void sendUserNotification(UserTicketsSubscription subscription, String priceChangeMessage, List<Car> updatedCars) {
        StringBuilder notificationMessage = buildNotificationMessage(subscription, priceChangeMessage, updatedCars);
        sendMessageService.sendMessage(subscription.getChatId(), notificationMessage.toString());
    }

    /**
     * Builds a notification message for the user.
     *
     * @param subscription      The user subscription.
     * @param priceChangeMessage The message indicating the price changes.
     * @param updatedCars        The list of updated cars.
     * @return The notification message.
     */
    private StringBuilder buildNotificationMessage(UserTicketsSubscription subscription, String priceChangeMessage, List<Car> updatedCars) {
        StringBuilder notificationMessage = new StringBuilder(messagesService.getReplyText("subscription.trainTicketsPriceChanges",
                Emojis.NOTIFICATION_BELL, subscription.getTrainNumber(), subscription.getTrainName(),
                subscription.getDateDepart(), subscription.getTimeDepart(), subscription.getStationArrival())).append(priceChangeMessage);

        notificationMessage.append(messagesService.getReplyText("subscription.lastTicketPrices"));

        for (Car car : updatedCars) {
            notificationMessage.append(messagesService.getReplyText("subscription.carsTicketsInfo", Emojis.BED,
                    car.getCarType(), Emojis.MINUS, car.getFreeSeats(), Emojis.MINUS, car.getMinimalPrice()));
        }

        return notificationMessage;
    }

    /**
     * Checks if the actual train and the subscribed train have the same train number and date.
     *
     * @param actualTrain  The actual train information.
     * @param subscription The user subscription.
     * @return True if the trains have the same number and date, false otherwise.
     */
    private boolean isSameTrainAndDate(Train actualTrain, UserTicketsSubscription subscription) {
        return actualTrain.getNumber().equals(subscription.getTrainNumber()) &&
                actualTrain.getDateDepart().equals(subscription.getDateDepart());
    }

    /**
     * Processes the lists of subscribed cars and actual cars, checking for changes and generating a notification message.
     *
     * @param subscribedCars The list of subscribed cars.
     * @param actualCars     The list of actual cars.
     * @return A map containing the notification message and the list of updated cars.
     */
    private Map<String, List<Car>> processCarsLists(List<Car> subscribedCars, List<Car> actualCars) {
        StringBuilder notificationMessage = new StringBuilder();

        for (Car subscribedCar : subscribedCars) {
            for (Car actualCar : actualCars) {
                if (actualCar.getCarType().equals(subscribedCar.getCarType())) {
                    processCar(subscribedCar, actualCar, notificationMessage);
                }
            }
        }

        return notificationMessage.isEmpty() ? Collections.emptyMap() : Collections.singletonMap(notificationMessage.toString(), subscribedCars);
    }

    /**
     * Processes a single car, checking for changes in price and free seats.
     *
     * @param subscribedCar     The subscribed car.
     * @param actualCar         The actual car.
     * @param notificationMessage The StringBuilder to append the notification message.
     */
    private void processCar(Car subscribedCar, Car actualCar, StringBuilder notificationMessage) {

        if (actualCar.getMinimalPrice() > subscribedCar.getMinimalPrice()) {
            appendNotificationMessage(notificationMessage, "subscription.PriceUp", Emojis.NOTIFICATION_PRICE_UP,
                    actualCar.getCarType(), subscribedCar.getMinimalPrice(), actualCar.getMinimalPrice());
            subscribedCar.setMinimalPrice(actualCar.getMinimalPrice());
        }

        else if (actualCar.getMinimalPrice() < subscribedCar.getMinimalPrice()) {
            appendNotificationMessage(notificationMessage, "subscription.PriceDown", Emojis.NOTIFICATION_PRICE_DOWN,
                    actualCar.getCarType(), subscribedCar.getMinimalPrice(), actualCar.getMinimalPrice());
            subscribedCar.setMinimalPrice(actualCar.getMinimalPrice());
        }

        subscribedCar.setFreeSeats(actualCar.getFreeSeats());
    }

    /**
     * Appends a price change notification message to the StringBuilder.
     *
     * @param notificationMessage The StringBuilder to append the message.
     * @param messageKey          The message key.
     * @param emoji               The emoji to include in the message.
     * @param carType             The car type.
     * @param oldPrice            The old price.
     * @param newPrice            The new price.
     */
    private void appendNotificationMessage(StringBuilder notificationMessage, String messageKey, Emojis emoji, String carType, int oldPrice, int newPrice) {
        notificationMessage.append(messagesService.getReplyText(messageKey, emoji, carType, oldPrice, newPrice));
    }

    /**
     * Parses a date string into a Date object.
     *
     * @param dateDeparture The date string to parse.
     * @return A Date object representing the parsed date.
     */
    private Date parseDateDeparture(String dateDeparture) {
        Date dateDepart = null;
        try {
            dateDepart = DATE_FORMAT.parse(dateDeparture);
        } catch (ParseException e) {
            log.error("Error parsing date: {}", e.getMessage());
        }
        return dateDepart;
    }
}