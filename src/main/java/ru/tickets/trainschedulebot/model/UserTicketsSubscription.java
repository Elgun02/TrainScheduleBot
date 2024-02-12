package ru.tickets.trainschedulebot.model;

import lombok.Data;
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

    private List<RailwayCarriage> subscribedCars;

    public UserTicketsSubscription(Long chatId, String trainNumber, String trainName, String stationDepart,
                                   String stationArrival, String dateDepart, String dateArrival, String timeDepart,
                                   String timeArrival, List<RailwayCarriage> subscribedCars) {
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getTrainNumber() {
        return trainNumber;
    }

    public void setTrainNumber(String trainNumber) {
        this.trainNumber = trainNumber;
    }

    public String getTrainName() {
        return trainName;
    }

    public void setTrainName(String trainName) {
        this.trainName = trainName;
    }

    public String getStationDepart() {
        return stationDepart;
    }

    public void setStationDepart(String stationDepart) {
        this.stationDepart = stationDepart;
    }

    public String getStationArrival() {
        return stationArrival;
    }

    public void setStationArrival(String stationArrival) {
        this.stationArrival = stationArrival;
    }

    public String getDateDepart() {
        return dateDepart;
    }

    public void setDateDepart(String dateDepart) {
        this.dateDepart = dateDepart;
    }

    public String getDateArrival() {
        return dateArrival;
    }

    public void setDateArrival(String dateArrival) {
        this.dateArrival = dateArrival;
    }

    public String getTimeDepart() {
        return timeDepart;
    }

    public void setTimeDepart(String timeDepart) {
        this.timeDepart = timeDepart;
    }

    public String getTimeArrival() {
        return timeArrival;
    }

    public void setTimeArrival(String timeArrival) {
        this.timeArrival = timeArrival;
    }

    public List<RailwayCarriage> getSubscribedCars() {
        return subscribedCars;
    }

    public void setSubscribedCars(List<RailwayCarriage> subscribedCars) {
        this.subscribedCars = subscribedCars;
    }

    @Override
    public String toString() {
        return "UserTicketsSubscription{" +
                "id='" + id + '\'' +
                ", chatId=" + chatId +
                ", trainNumber='" + trainNumber + '\'' +
                ", trainName='" + trainName + '\'' +
                ", stationDepart='" + stationDepart + '\'' +
                ", stationArrival='" + stationArrival + '\'' +
                ", dateDepart='" + dateDepart + '\'' +
                ", dateArrival='" + dateArrival + '\'' +
                ", timeDepart='" + timeDepart + '\'' +
                ", timeArrival='" + timeArrival + '\'' +
                ", subscribedCars=" + subscribedCars +
                '}';
    }
}
