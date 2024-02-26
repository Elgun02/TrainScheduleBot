package ru.tickets.trainschedulebot.botApi.handlers.trainsearch;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TrainSearchRequestData {
    private String departureStation;

    private String arrivalStation;

    private Integer departureStationCode;

    private Integer arrivalStationCode;

    private Date dateDepart;
}