package ru.tickets.trainschedulebot.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

/**
 * Train Station
 *
 * @author Elgun Dilanchiev
 */

@Getter
@ToString
public class TrainStation {
    @JsonProperty(value = "n")
    private String stationName;

    @JsonProperty(value = "c")
    private Integer stationCode;
}