package ru.tickets.trainschedulebot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.tickets.trainschedulebot.botApi.TelegramBot;

import java.util.ArrayList;
import java.util.List;

/**
 * Service class for sending messages, inline keyboards, and updating messages in Telegram.
 * This class interacts with a {@link TelegramBot} to perform various messaging operations.
 *
 * @author Elgun Dilanchiev
 * @version 1.0
 * @since 2024-02-28
 */
@Slf4j
@Service
public class SendMessageService {

    /**
     * The TelegramBot instance used for sending messages.
     */
    private final TelegramBot telegramBot;

    /**
     * Constructs a new SendMessageService with the specified TelegramBot.
     *
     * @param telegramBot The TelegramBot instance used for sending messages.
     */
    public SendMessageService(@Lazy TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    /**
     * Sends an inline keyboard message with the specified chat ID, message text, button text, and callback data.
     *
     * @param chatId        The ID of the chat to which the message will be sent.
     * @param messageText   The text of the message.
     * @param buttonText    The text to be displayed on the inline keyboard button.
     * @param callbackData  The callback data associated with the inline keyboard button.
     */
    public void sendInlineKeyBoardMessage(long chatId, String messageText, String buttonText, String callbackData) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setParseMode(ParseMode.MARKDOWN);
        sendMessage.setChatId(chatId);
        sendMessage.setText(messageText);

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = getKeyboardButtonList(buttonText, callbackData);

        inlineKeyboardMarkup.setKeyboard(rowsInline);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);

        sendMessage(sendMessage);
    }

    /**
     * Retrieves a list of inline keyboard buttons based on the specified button text and callback data.
     *
     * @param buttonText    The text to be displayed on the inline keyboard button.
     * @param callBackData  The callback data associated with the inline keyboard button.
     * @return A list containing a row of inline keyboard buttons.
     */
    private static List<List<InlineKeyboardButton>> getKeyboardButtonList(String buttonText, String callBackData) {
        List<InlineKeyboardButton> rowInline = new ArrayList<>();

        InlineKeyboardButton button1 = new InlineKeyboardButton();
        button1.setText("Купить");
        button1.setUrl("www.google.com");

        InlineKeyboardButton button2 = new InlineKeyboardButton();
        button2.setText(buttonText);
        button2.setCallbackData(callBackData);

        rowInline.add(button1);
        rowInline.add(button2);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(rowInline);
        return rowList;
    }

    /**
     * Update and send inline keyboard message based on the provided CallbackQuery, button text, and callback data.
     *
     * @param callbackQuery The CallbackQuery containing the necessary information.
     * @param buttonText    The text to be displayed on the inline keyboard button.
     * @param callbackData  The callback data associated with the inline keyboard button.
     */
    public void updateAndSendInlineKeyBoardMessage(CallbackQuery callbackQuery, String buttonText, String callbackData) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(getKeyboardButtonList(buttonText, callbackData));

        final long messageId = callbackQuery.getMessage().getMessageId();
        final long chatId = callbackQuery.getMessage().getChatId();

        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(chatId);
        editMessageText.setMessageId(Math.toIntExact(messageId));
        editMessageText.setText(callbackQuery.getMessage().getText());
        editMessageText.setReplyMarkup(inlineKeyboardMarkup);

        try {
            telegramBot.execute(editMessageText);
        } catch (TelegramApiException e) {
            log.error("Error occurred while sending Telegram Edit Message. Details: " + e.getMessage(), e);
        }
    }

    /**
     * Sends the specified SendMessage
     *
     * @param sendMessage The SendMessage object to be sent.
     */
    public void sendMessage(SendMessage sendMessage) {
        try {
            telegramBot.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Error occurred while sending Telegram message. Details: " + e.getMessage(), e);
        }
    }

    /**
     * Sends a simple text message with the specified chat ID and text.
     *
     * @param chatId      The ID of the chat to which the message will be sent.
     * @param textMessage The text of the message.
     */
    public void sendMessage(long chatId, String textMessage) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(textMessage);

        sendMessage(sendMessage);
    }
}