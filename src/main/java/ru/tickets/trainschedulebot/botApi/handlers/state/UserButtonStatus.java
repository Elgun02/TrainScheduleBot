package ru.tickets.trainschedulebot.botApi.handlers.state;

import lombok.AllArgsConstructor;


/**
 * Enum representing user subscription button statuses.
 * The enum provides button status values for subscribed and unsubscribed states.
 * It also overrides the toString method to return the corresponding button status.
 *
 * @author Elgun Dilanchiev
 * @version 1.0
 * @since 2024-02-29
 */
@AllArgsConstructor
public enum UserButtonStatus {
    SUBSCRIBED("Отписаться"), UNSUBSCRIBED("Подписаться");

    private final String buttonStatus;

    /**
     * Returns the string representation of the button status.
     *
     * @return String representation of the button status.
     */
    @Override
    public String toString() {
        return buttonStatus;
    }
}
