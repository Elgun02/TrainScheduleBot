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


@Slf4j
@Service
public class SendMessageService {
    private final TelegramBot telegramBot;

    public SendMessageService(@Lazy TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

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

    public void sendMessage(SendMessage sendMessage) {
        try {
            telegramBot.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Error occurred while sending Telegram message. Details: " + e.getMessage(), e);
        }
    }

    public void sendMessage(long chatId, String textMessage) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(textMessage);

        sendMessage(sendMessage);
    }
}