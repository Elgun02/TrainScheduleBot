package ru.tickets.trainschedulebot.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Train carriage
 *
 * @author Elgun Dilanchiev
 */

@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RailwayCarriage {

    @JsonProperty(value = "type")
    private String carType;

    @JsonProperty(value = "free_seats")
    private Integer freeSeats;

    @JsonProperty(value = "tariff")
    private Integer minimalPrice;

    public String getCarType() {
        return carType;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }

    public Integer getFreeSeats() {
        return freeSeats;
    }

    public void setFreeSeats(Integer freeSeats) {
        this.freeSeats = freeSeats;
    }

    public Integer getMinimalPrice() {
        return minimalPrice;
    }

    public void setMinimalPrice(Integer minimalPrice) {
        this.minimalPrice = minimalPrice;
    }

    @Override
    public String toString() {
        return "RailwayCarriage{" +
                "carType='" + carType + '\'' +
                ", freeSeats=" + freeSeats +
                ", minimalPrice=" + minimalPrice +
                '}';
    }
}
