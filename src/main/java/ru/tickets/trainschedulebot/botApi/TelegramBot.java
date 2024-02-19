package ru.tickets.trainschedulebot.botApi;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TelegramBot extends TelegramWebhookBot {

    private String botPath;
    private String botUsername;
    private String botToken;

    private TelegramFacade telegramFacade;

    public TelegramBot(DefaultBotOptions options, TelegramFacade telegramFacade) {
        super(options);
        this.telegramFacade = telegramFacade;
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        return telegramFacade.handleUpdate(update);
    }

    public void sendMessage(long chatId, String textMessage) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(textMessage);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.warn("Ошибка при запуске метода execute(). Причина ошибка: {}", e.getMessage());
        }
    }

    public void sendMessage(SendMessage sendMessage) {
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.warn("Ошибка при запуске метода execute(). Причина ошибка: {}", e.getMessage());
        }
    }

    public void sendInlineKeyBoardMessage(long chatId, String messageText, String buttonText, String callbackData) {
        log.info("Запустился метод 'sendInlineKeyboardMessage', \n" +
                "Chat ID: {}\n" +
                "Message Text: {}", chatId, messageText);

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton keyboardButton = new InlineKeyboardButton();
        keyboardButton.setText(buttonText);

        if (callbackData != null) {
            keyboardButton.setCallbackData(callbackData);
        }

        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(keyboardButton);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);

        inlineKeyboardMarkup.setKeyboard(rowList);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(messageText);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Ошибка при запуске метода 'execute' - {},\n {}", e.getMessage(), e.getLocalizedMessage());
        }
    }


    public void sendChangedInlineButtonText(CallbackQuery callbackQuery, String buttonText, String callbackData) {
        final InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        final List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        final long message_id = Long.parseLong(callbackQuery.getInlineMessageId());
        final long chat_id = callbackQuery.getMessage().getChatId();
        final List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton(buttonText);
        inlineKeyboardButton.setCallbackData(callbackData);

        keyboardButtonsRow1.add(inlineKeyboardButton);
        rowList.add(keyboardButtonsRow1);
        inlineKeyboardMarkup.setKeyboard(rowList);

        EditMessageText editMessageText = new EditMessageText();
        Message message = (Message) callbackQuery.getMessage();

        editMessageText.setChatId(chat_id);
        editMessageText.setMessageId((int) message_id);
        editMessageText.setText(message.getText());
        editMessageText.setReplyMarkup(inlineKeyboardMarkup);

        try {
            execute(editMessageText);
        } catch (TelegramApiException e) {
            log.warn("Ошибка при запуске метода execute(). Причина ошибка: {}", e.getMessage());
        }
    }
}
