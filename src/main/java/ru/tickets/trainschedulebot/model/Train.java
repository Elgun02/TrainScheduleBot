package ru.tickets.trainschedulebot.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

/**
 * Train
 *
 * @author Elgun Dilanchiev
 */

@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Train {

    @JsonProperty(value = "number")
    private String number;

    @JsonProperty(value = "brand")
    private String brand;

    @JsonProperty(value = "departureStation")
    private String stationDepart;

    @JsonProperty(value = "arrivalStation")
    private String stationArrival;

    @JsonProperty(value = "departureDate")
    private String dateDepart;

    @JsonProperty(value = "arrivalDate")
    private String dateArrival;

    @JsonProperty(value = "departureTime")
    private String timeDepart;

    @JsonProperty(value = "arrivalTime")
    private String timeArrival;

    @JsonProperty(value = "timeInWay")
    private String timeInWay;

    @JsonProperty(value = "carriages")
    private List<RailwayCarriage> availableCarriages;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
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

    public String getTimeInWay() {
        return timeInWay;
    }

    public void setTimeInWay(String timeInWay) {
        this.timeInWay = timeInWay;
    }

    public List<RailwayCarriage> getAvailableCarriages() {
        return availableCarriages;
    }

    public void setAvailableCarriages(List<RailwayCarriage> availableCarriages) {
        this.availableCarriages = availableCarriages;
    }
}
