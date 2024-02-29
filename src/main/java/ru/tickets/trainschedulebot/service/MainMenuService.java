package ru.tickets.trainschedulebot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

/**
 * Service class for handling the main menu of the Telegram bot.
 * This class provides methods to generate a main menu message with a custom keyboard.
 *
 * @author Elgun Dilanchiev
 * @version 1.0
 * @since 2024-02-28
 */
@Service
@RequiredArgsConstructor
public class MainMenuService {

    /**
     * Gets the main menu message with the provided chat ID and text message.
     *
     * @param chatId        The ID of the chat to which the message will be sent.
     * @param textMessage   The text message to be included in the main menu.
     * @return A SendMessage object representing the main menu message with a custom keyboard.
     */
    public SendMessage getMainMenuMessage(final long chatId, final String textMessage) {
        final ReplyKeyboardMarkup replyKeyboardMarkup = getMainMenuKeyboard();
        return createMessageWithKeyboard(chatId, textMessage, replyKeyboardMarkup);
    }

    /**
     * Generates the main menu keyboard with predefined buttons.
     *
     * @return A ReplyKeyboardMarkup object representing the main menu keyboard.
     */
    private ReplyKeyboardMarkup getMainMenuKeyboard() {
        final ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        KeyboardRow row2 = new KeyboardRow();
        KeyboardRow row3 = new KeyboardRow();
        KeyboardRow row4 = new KeyboardRow();

        String[] rows = getButtonsForMainMenu();

        row1.add(new KeyboardButton(rows[0]));
        row2.add(new KeyboardButton(rows[1]));
        row3.add(new KeyboardButton(rows[2]));
        row4.add(new KeyboardButton(rows[3]));

        keyboard.add(row1);
        keyboard.add(row2);
        keyboard.add(row3);
        keyboard.add(row4);

        replyKeyboardMarkup.setKeyboard(keyboard);
        return replyKeyboardMarkup;
    }

    /**
     * Creates a SendMessage object with the provided chat ID, text message, and custom keyboard.
     *
     * @param chatId              The ID of the chat to which the message will be sent.
     * @param textMessage         The text message to be included in the main menu.
     * @param replyKeyboardMarkup The custom keyboard to be included in the message.
     * @return A SendMessage object representing the main menu message with a custom keyboard.
     */
    private SendMessage createMessageWithKeyboard(long chatId, String textMessage, ReplyKeyboardMarkup replyKeyboardMarkup) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId);
        sendMessage.setText(textMessage);

        if (replyKeyboardMarkup != null) {
            sendMessage.setReplyMarkup(replyKeyboardMarkup);
        }

        return sendMessage;
    }

    /**
     * Gets an array of button labels for the main menu.
     *
     * @return An array of button labels representing options in the main menu.
     */
    private String[] getButtonsForMainMenu() {
            return new String[] {"Найти поезда", "Мои подписки", "Справочник ст.", "Помощь"};
    }
}