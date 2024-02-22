package ru.tickets.trainschedulebot.botApi.handlers.callbackquery;

import lombok.AllArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@ToString
public enum ButtonStatus {
    SUBSCRIBED("Подписаться"), UNSUBSCRIBED("Отписаться");

    private final String buttonStatus;
}

