package ru.tickets.trainschedulebot.botApi.handlers.state;

import lombok.AllArgsConstructor;


@AllArgsConstructor
public enum UserButtonStatus {
    SUBSCRIBED("Отписаться"), UNSUBSCRIBED("Подписаться");

    private final String buttonStatus;

    @Override
    public String toString() {
        return buttonStatus;
    }
}