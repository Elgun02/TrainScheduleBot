package ru.tickets.trainschedulebot.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * Train carriage
 *
 * @author Elgun Dilanchiev
 */

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RailwayCarriage {

    @JsonProperty(value = "type")
    private String carType;

    @JsonProperty(value = "freeSeats")
    private Integer freeSeats;

    @JsonProperty(value = "tariff")
    private Integer minimalPrice;

}
