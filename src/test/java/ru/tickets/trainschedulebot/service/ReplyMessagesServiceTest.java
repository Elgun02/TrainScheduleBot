package ru.tickets.trainschedulebot.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.tickets.trainschedulebot.utils.Emojis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReplyMessagesServiceTest {

    @Mock
    LocaleMessageService localeMessageService;

    @InjectMocks
    ReplyMessagesService replyMessagesService;

    private long chatId;

    @BeforeEach
    void setUp() {
        chatId = 999999999L;
    }

    @Test
    void testGetReplyMessage_shouldReturnSendMessageWithLocalizedText() {
        String replyMessageCode = "test.reply.message";
        String localizedText = "Test message text";
        when(localeMessageService.getMessage(replyMessageCode)).thenReturn(localizedText);
        SendMessage sendMessage = replyMessagesService.getReplyMessage(chatId, replyMessageCode);
        assertEquals(String.valueOf(chatId), sendMessage.getChatId());
        assertEquals(localizedText, sendMessage.getText());
    }

    @Test
    void testGetReplyMessage_shouldReturnSendMessageWithLocalizedTextAndArgs() {
        String replyMessageCode = "test.reply.message.args";
        String localizedText = "Test message text with args {0}, {1}";
        when(localeMessageService.getMessage(replyMessageCode, "args1", "args2")).thenReturn(localizedText);
        SendMessage sendMessage = replyMessagesService.getReplyMessage(chatId, replyMessageCode, "args1", "args2");
        assertEquals(String.valueOf(chatId), sendMessage.getChatId());
        assertEquals(localizedText, sendMessage.getText());
    }

    @Test
    void testGetEmojiReplyText_shouldReturnSendMessageWithLocalizedTextAndEmoji() {
        String replyMessageCode = "test.emoji.reply.message";
        String localizedText = "{0} Test message with Emoji";
        when(localeMessageService.getMessage(replyMessageCode, Emojis.HELP_MENU_WELCOME)).thenReturn(localizedText);
        String replyText = replyMessagesService.getEmojiReplyText(replyMessageCode, Emojis.HELP_MENU_WELCOME);
        assertEquals(localizedText, replyText);
    }
 }
