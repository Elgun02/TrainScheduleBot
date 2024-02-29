package ru.tickets.trainschedulebot.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Represents a train station with a name and a station code.
 * This class is annotated with Jackson annotations for JSON property mappings.
 *
 * @author Elgun Dilanchiev
 * @version 1.0
 * @since 2024-02-28
 */

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class TrainStation {
    /**
     * The name of the train station.
     */
    @JsonProperty(value = "n")
    private String stationName;

    /**
     * The code assigned to the train station.
     */
    @JsonProperty(value = "c")
    private Integer stationCode;
}