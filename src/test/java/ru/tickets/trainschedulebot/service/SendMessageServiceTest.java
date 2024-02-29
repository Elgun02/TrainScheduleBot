package ru.tickets.trainschedulebot.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.tickets.trainschedulebot.botApi.TelegramBot;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SendMessageServiceTest {

    @Mock
    private TelegramBot telegramBot;

    @InjectMocks
    private SendMessageService sendMessageService;

    @Test
    void sendMessage_shouldSendSimpleTextMessage() {
        long chatId = 999999999L;
        String textMessage = "Test message";

        sendMessageService.sendMessage(chatId, textMessage);
        SendMessage expectedSendMessage = new SendMessage();
        expectedSendMessage.setChatId(chatId);
        expectedSendMessage.setText(textMessage);

        try {
            verify(telegramBot, times(1)).execute(expectedSendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}