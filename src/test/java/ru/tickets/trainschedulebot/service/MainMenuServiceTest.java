package ru.tickets.trainschedulebot.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MainMenuServiceTest {

    @InjectMocks
    private MainMenuService mainMenuService;

    @Test
    void testGetMainMenuMessage() {
        long chatId = 99999999L;
        String textMessage = "Test Message";

        SendMessage sendMessage = mainMenuService.getMainMenuMessage(chatId, textMessage);

        String messageChatId = sendMessage.getChatId();

        assertNotNull(sendMessage);
        assertNotNull(sendMessage.getReplyMarkup());
        assertEquals(messageChatId, sendMessage.getChatId());
        assertEquals(textMessage, sendMessage.getText());
        assertTrue(sendMessage.getReplyMarkup() instanceof ReplyKeyboardMarkup);

        int buttonSize = ((ReplyKeyboardMarkup) sendMessage.getReplyMarkup()).getKeyboard().size();

        assertEquals(4, buttonSize);
    }
}
