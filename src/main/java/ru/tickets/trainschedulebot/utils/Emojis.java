package ru.tickets.trainschedulebot.utils;

import com.vdurmont.emoji.EmojiParser;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Emojis {
    TRAIN(EmojiParser.parseToUnicode(":steam_locomotive:")),
    TIME_DEPART(EmojiParser.parseToUnicode(":clock8:")),
    TIME_ARRIVAL(EmojiParser.parseToUnicode(":clock3:")),
    TIME_IN_WAY(EmojiParser.parseToUnicode(":clock5:")),
    MINUS(EmojiParser.parseToUnicode(":heavy_minus_sign:")),
    BED(EmojiParser.parseToUnicode(":bed:")),
    SUCCESS_MARK(EmojiParser.parseToUnicode(":white_check_mark:")),
    SWITCH_LOCALE_ARROW(EmojiParser.parseToUnicode(":arrow_down:")),
    NOTIFICATION_MARK_FAILED(EmojiParser.parseToUnicode(":exclamation:")),
    SUCCESS_UNSUBSCRIBED(EmojiParser.parseToUnicode(":negative_squared_cross_mark:")),
    SUCCESS_SUBSCRIBED(EmojiParser.parseToUnicode(":mailbox:")),
    NOTIFICATION_BELL(EmojiParser.parseToUnicode(":bell:")),
    NOTIFICATION_INFO_MARK(EmojiParser.parseToUnicode(":information_source:")),
    NOTIFICATION_PRICE_UP(EmojiParser.parseToUnicode(":chart_with_upwards_trend:")),
    NOTIFICATION_PRICE_DOWN(EmojiParser.parseToUnicode(":chart_with_downwards_trend:")),
    HELP_MENU_WELCOME(EmojiParser.parseToUnicode(":hatched_chick:"));

    private final String emoji;

    @Override
    public String toString() {
        return emoji;
    }
}
