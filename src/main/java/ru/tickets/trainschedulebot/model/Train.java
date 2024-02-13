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

@Setter
@Getter
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

}
