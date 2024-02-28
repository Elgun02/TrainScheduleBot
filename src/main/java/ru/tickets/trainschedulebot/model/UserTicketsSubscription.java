package ru.tickets.trainschedulebot.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * Represents a user's subscription to receive notifications about train tickets.
 * This class is annotated with Lombok {@code @Data} annotation for automatic
 * generation of getters, setters, equals, hashCode, and toString methods.
 * <p>
 * The subscriptions are stored in the MongoDB collection named "subscriptions".
 *
 * @author Elgun Dilanbchiev
 * @version 1.0
 * @since 2024-02-28
 */
@Data
@Document(collection = "subscriptions")
public class UserTicketsSubscription {
    /**
     * The unique identifier for the subscription.
     */
    @Id
    private String id;

    /**
     * The chat ID associated with the user.
     */
    private long chatId;

    /**
     * The train number of the subscribed train.
     */
    private String trainNumber;

    /**
     * The name of the subscribed train.
     */
    private String trainName;

    /**
     * The departure station of the subscribed train.
     */
    private String stationDepart;

    /**
     * The arrival station of the subscribed train.
     */
    private String stationArrival;

    /**
     * The departure date of the subscribed train.
     */
    private String dateDepart;

    /**
     * The arrival date of the subscribed train.
     */
    private String dateArrival;

    /**
     * The departure time of the subscribed train.
     */
    private String timeDepart;

    /**
     * The arrival time of the subscribed train.
     */
    private String timeArrival;

    /**
     * The list of cars to which the user is subscribed for notifications.
     */
    private List<Car> subscribedCars;

    /**
     * Constructs a new UserTicketsSubscription with the specified parameters.
     *
     * @param chatId          The chat ID associated with the user.
     * @param trainNumber     The train number of the subscribed train.
     * @param trainName       The name of the subscribed train.
     * @param stationDepart   The departure station of the subscribed train.
     * @param stationArrival  The arrival station of the subscribed train.
     * @param dateDepart      The departure date of the subscribed train.
     * @param dateArrival     The arrival date of the subscribed train.
     * @param timeDepart      The departure time of the subscribed train.
     * @param timeArrival     The arrival time of the subscribed train.
     * @param subscribedCars  The list of cars to which the user is subscribed.
     */
    public UserTicketsSubscription(long chatId, String trainNumber, String trainName, String stationDepart,
                                   String stationArrival, String dateDepart, String dateArrival, String timeDepart,
                                   String timeArrival, List<Car> subscribedCars) {
        this.chatId = chatId;
        this.trainNumber = trainNumber;
        this.trainName = trainName;
        this.stationDepart = stationDepart;
        this.stationArrival = stationArrival;
        this.dateDepart = dateDepart;
        this.dateArrival = dateArrival;
        this.timeDepart = timeDepart;
        this.timeArrival = timeArrival;
        this.subscribedCars = subscribedCars;
    }
}