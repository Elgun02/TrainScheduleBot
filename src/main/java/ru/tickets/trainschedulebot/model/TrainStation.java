package ru.tickets.trainschedulebot.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
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

    @JsonProperty(value = "station_name")
    private String stationName;

    @JsonProperty(value = "station_code")
    private Integer stationCode;
}
