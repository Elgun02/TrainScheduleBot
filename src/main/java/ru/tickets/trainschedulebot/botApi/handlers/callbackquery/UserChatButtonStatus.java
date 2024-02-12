package ru.tickets.trainschedulebot.botApi.handlers.callbackquery;

import lombok.AllArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@ToString
public enum UserChatButtonStatus {
    SUBSCRIBED("Follow"), UNSUBSCRIBED("Unfollow");

    private final String buttonStatus;
}

