package ru.tickets.trainschedulebot.botApi.handlers.trainsearch;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.Date;

/**
 * Data class representing the user's input data during the train search conversation flow.
 * It contains information such as the departure and arrival stations, station codes, and the date of departure.
 *
 * @author Elgun Dilanchiev
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TrainSearchRequestData {
    /**
     * The name of the departure station.
     */
    private String departureStation;

    /**
     * The name of the arrival station.
     */
    private String arrivalStation;

    /**
     * The code of the departure station.
     */
    private Integer departureStationCode;

    /**
     * The code of the arrival station.
     */
    private Integer arrivalStationCode;

    /**
     * The date of departure.
     */
    private Date dateDepart;
}
