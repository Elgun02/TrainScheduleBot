package ru.tickets.trainschedulebot.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Train Station
 *
 * @author Elgun Dilanchiev
 */

public class TrainStation {

    @JsonProperty(value = "station_name")
    private String stationName;

    @JsonProperty(value = "station_code")
    private Integer stationCode;

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public Integer getStationCode() {
        return stationCode;
    }

    public void setStationCode(Integer stationCode) {
        this.stationCode = stationCode;
    }

    @Override
    public String toString() {
        return "TrainStation{" +
                "stationName='" + stationName + '\'' +
                ", stationCode=" + stationCode +
                '}';
    }
}
