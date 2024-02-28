package ru.tickets.trainschedulebot.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

/**
 * The {@code Train} class represents a train with information such as train number, brand,
 * departure and arrival stations, departure and arrival dates and times, available cars,
 * and the time it takes to travel.
 * <p>
 * This class is annotated with Lombok annotations to automatically generate getter, setter,
 * toString, and constructor methods. It is also annotated with Jackson's annotations for JSON
 * serialization and deserialization, including {@link JsonIgnoreProperties} to ignore unknown properties.
 * <p>
 * The {@link JsonProperty} annotation is used for mapping JSON properties to class fields during deserialization.
 *
 * @author Elgun Dilanchiev
 * @version 1.0
 * @since 2024-02-28
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Train {
    /**
     * The number of the train.
     */
    @JsonProperty(value = "number")
    private String number;

    /**
     * The brand of the train.
     */
    @JsonProperty(value = "brand")
    private String brand;

    /**
     * The departure station of the train.
     */
    @JsonProperty(value = "station0")
    private String stationDepart;

    /**
     * The arrival station of the train.
     */
    @JsonProperty(value = "station1")
    private String stationArrival;

    /**
     * The departure date of the train.
     */
    @JsonProperty(value = "date0")
    private String dateDepart;

    /**
     * The arrival date of the train.
     */
    @JsonProperty(value = "date1")
    private String dateArrival;

    /**
     * The departure time of the train.
     */
    @JsonProperty(value = "time0")
    private String timeDepart;

    /**
     * The arrival time of the train.
     */
    @JsonProperty(value = "time1")
    private String timeArrival;

    /**
     * The list of available train carriages for the train.
     */
    @JsonProperty(value = "cars")
    private List<Car> availableCars;

    /**
     * The time it takes for the train to travel.
     */
    @JsonProperty(value = "timeInWay")
    private String timeInWay;
}