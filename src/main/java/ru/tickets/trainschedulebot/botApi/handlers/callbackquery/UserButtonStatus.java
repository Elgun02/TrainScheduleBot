package ru.tickets.trainschedulebot.botApi.handlers.callbackquery;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum UserButtonStatus {
    SUBSCRIBED("Отписаться"), UNSUBSCRIBED("Подписаться"),
    EN("SWITCH_TO_RU"), RU("SWITCH_TO_EN");

    private final String buttonStatus;

    @Override
    public String toString() {
        return buttonStatus;
    }
}

