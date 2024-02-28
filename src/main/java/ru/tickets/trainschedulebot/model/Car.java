package ru.tickets.trainschedulebot.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * The {@code Car} class represents a train carriage with information such as car type,
 * the number of free seats, and the minimal price.
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
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Car {
    /**
     * The type of the train carriage.
     */
    @JsonProperty(value = "type")
    private String carType;

    /**
     * The number of free seats in the train carriage.
     */
    @JsonProperty(value = "freeSeats")
    private Integer freeSeats;

    /**
     * The minimal price tariff for the train carriage.
     */
    @JsonProperty(value = "tariff")
    private Integer minimalPrice;
}