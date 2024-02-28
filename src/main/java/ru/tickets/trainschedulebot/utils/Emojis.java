package ru.tickets.trainschedulebot.utils;

import com.vdurmont.emoji.EmojiParser;
import lombok.AllArgsConstructor;

/**
 * An enumeration of emojis used in the train schedule bot for various purposes.
 * Each enum constant represents a specific emoji with its corresponding Unicode representation.
 */
@AllArgsConstructor
public enum Emojis {
    TRAIN(EmojiParser.parseToUnicode(":steam_locomotive:")),
    MINUS(EmojiParser.parseToUnicode(":heavy_minus_sign:")),
    BED(EmojiParser.parseToUnicode(":bed:")),
    SUCCESS_MARK(EmojiParser.parseToUnicode(":white_check_mark:")),
    NOTIFICATION_MARK_FAILED(EmojiParser.parseToUnicode(":exclamation:")),
    SUCCESS_UNSUBSCRIBED(EmojiParser.parseToUnicode(":small_red_triangle_down:")),
    SUCCESS_SUBSCRIBED(EmojiParser.parseToUnicode(":small_blue_diamond:")),
    NOTIFICATION_BELL(EmojiParser.parseToUnicode(":bell:")),
    NOTIFICATION_PRICE_UP(EmojiParser.parseToUnicode(":chart_with_upwards_trend:")),
    NOTIFICATION_PRICE_DOWN(EmojiParser.parseToUnicode(":chart_with_downwards_trend:")),
    HELP_MENU_WELCOME(EmojiParser.parseToUnicode(":hatched_chick:"));

    private final String emoji;

    @Override
    public String toString() {
        return emoji;
    }
}