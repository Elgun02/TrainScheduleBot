package ru.tickets.trainschedulebot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.tickets.trainschedulebot.utils.Emojis;

/**
 * Service class for generating reply messages in Telegram.
 * This class provides methods to create reply messages with or without localization, and includes emojis for success and warning messages.
 *
 * @author Elgun Dilanchiev
 * @version 1.0
 * @since 2024-02-28
 */
@Service
@RequiredArgsConstructor
public class ReplyMessagesService {
    private final LocaleMessageService localeMessageService;

    /**
     * Generates a reply message for the specified chat ID and reply message code.
     *
     * @param chatId       The ID of the chat to which the message will be sent.
     * @param replyMessage The code for the desired reply message.
     * @return A SendMessage object representing the reply message.
     */
    public SendMessage getReplyMessage(long chatId, String replyMessage) {
        return new SendMessage(String.valueOf(chatId), localeMessageService.getMessage(replyMessage));
    }

    /**
     * Generates a reply message for the specified chat ID, reply message code, and parameterized arguments.
     *
     * @param chatId       The ID of the chat to which the message will be sent.
     * @param replyMessage The code for the desired reply message.
     * @param args         The arguments to be replaced in the message placeholders.
     * @return A SendMessage object representing the reply message with substituted arguments.
     */
    public SendMessage getReplyMessage(long chatId, String replyMessage, Object... args) {
        return new SendMessage(String.valueOf(chatId), localeMessageService.getMessage(replyMessage, args));
    }

    /**
     * Generates a success reply message for the specified chat ID and reply message code, including a success emoji.
     *
     * @param chatId       The ID of the chat to which the message will be sent.
     * @param replyMessage The code for the desired success reply message.
     * @return A SendMessage object representing the success reply message with a success emoji.
     */
    public SendMessage getSuccessReplyMessage(long chatId, String replyMessage) {
        return new SendMessage(String.valueOf(chatId), getEmojiReplyText(replyMessage, Emojis.SUCCESS_MARK));
    }

    /**
     * Generates a warning reply message for the specified chat ID and reply message code, including a warning emoji.
     *
     * @param chatId       The ID of the chat to which the message will be sent.
     * @param replyMessage The code for the desired warning reply message.
     * @return A SendMessage object representing the warning reply message with a warning emoji.
     */
    public SendMessage getWarningReplyMessage(long chatId, String replyMessage) {
        return new SendMessage(String.valueOf(chatId), getEmojiReplyText(replyMessage, Emojis.NOTIFICATION_MARK_FAILED));
    }

    /**
     * Retrieves a localized reply text for the specified reply text code.
     *
     * @param replyText The code for the desired reply text.
     * @return The localized reply text.
     */
    public String getReplyText(String replyText) {
        return localeMessageService.getMessage(replyText);
    }

    /**
     * Retrieves a localized reply text for the specified reply text code with parameterized arguments.
     *
     * @param replyText The code for the desired reply text.
     * @param args      The arguments to be replaced in the message placeholders.
     * @return The localized reply text with substituted arguments.
     */
    public String getReplyText(String replyText, Object... args) {
        return localeMessageService.getMessage(replyText, args);
    }

    /**
     * Retrieves a reply text with an emoji for the specified reply text code and emoji.
     *
     * @param replyText The code for the desired reply text.
     * @param emoji     The emoji to be included in the reply text.
     * @return The reply text with the specified emoji.
     */
    public String getEmojiReplyText(String replyText, Emojis emoji) {
        return localeMessageService.getMessage(replyText, emoji);
    }
}