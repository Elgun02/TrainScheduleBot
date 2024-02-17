package ru.tickets.trainschedulebot.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

/**
 * Subscribe a user to a specific train
 *
 * @author Elgun Dilanchiev
 */

@Getter
@Setter
@ToString
@Document(collection = "usersTicketsSubscription")
public class UserTicketsSubscription {

    @Id
    private String id;

    private Long chatId;

    private String trainNumber;

    private String trainName;

    private String stationDepart;

    private String stationArrival;

    private String dateDepart;

    private String dateArrival;

    private String timeDepart;

    private String timeArrival;

    private List<RailwayCarriage> subscribedCarriages;

    public UserTicketsSubscription(Long chatId, String trainNumber, String trainName, String stationDepart,
                                   String stationArrival, String dateDepart, String dateArrival, String timeDepart,
                                   String timeArrival, List<RailwayCarriage> subscribedCarriages) {
        this.chatId = chatId;
        this.trainNumber = trainNumber;
        this.trainName = trainName;
        this.stationDepart = stationDepart;
        this.stationArrival = stationArrival;
        this.dateDepart = dateDepart;
        this.dateArrival = dateArrival;
        this.timeDepart = timeDepart;
        this.timeArrival = timeArrival;
        this.subscribedCarriages = subscribedCarriages;
    }
}
