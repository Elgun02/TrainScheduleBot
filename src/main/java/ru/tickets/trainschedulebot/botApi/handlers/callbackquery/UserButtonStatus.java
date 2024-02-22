package ru.tickets.trainschedulebot.botApi.handlers.callbackquery;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum UserButtonStatus {
    SUBSCRIBE("Отписаться"), UNSUBSCRIBE("Подписаться");

    private final String buttonStatus;

    @Override
    public String toString() {
        return buttonStatus;
    }
}

