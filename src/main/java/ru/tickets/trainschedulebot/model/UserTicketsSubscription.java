package ru.tickets.trainschedulebot.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "subscriptions")
public class UserTicketsSubscription {

    @Id
    private String id;

    private long chatId;

    private String trainNumber;

    private String trainName;

    private String stationDepart;

    private String stationArrival;

    private String dateDepart;

    private String dateArrival;

    private String timeDepart;

    private String timeArrival;

    private List<Car> subscribedCars;

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