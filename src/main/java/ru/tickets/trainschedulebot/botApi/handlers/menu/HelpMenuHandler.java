package ru.tickets.trainschedulebot.botApi.handlers.menu;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.tickets.trainschedulebot.botApi.handlers.state.BotState;
import ru.tickets.trainschedulebot.botApi.handlers.InputMessageHandler;
import ru.tickets.trainschedulebot.service.MainMenuService;
import ru.tickets.trainschedulebot.service.ReplyMessagesService;
import ru.tickets.trainschedulebot.utils.Emojis;

/**
 * Handles user requests for help menu.
 *
 * @author Elgun Dilanchiev
 */
@Component
@RequiredArgsConstructor
public class HelpMenuHandler implements InputMessageHandler {
    private final MainMenuService mainMenuService;
    private final ReplyMessagesService messagesService;

    /**
     * Handles user's message requesting help menu.
     *
     * @param message The Telegram message received from the user.
     * @return SendMessage object with the response to the help menu request.
     *
     * @author Elgun Dilanchiev
     * @since 2024-02-29
     */
    @Override
    public SendMessage handle(Message message) {
        return mainMenuService.getMainMenuMessage(message.getChatId(),
                messagesService.getEmojiReplyText("reply.helpMenu.welcomeMessage", Emojis.MINUS));
    }

    /**
     * Gets the handler's name representing the state.
     *
     * @return BotState representing the handler's name.
     */
    @Override
    public BotState getHandlerName() {
        return BotState.SHOW_HELP_MENU;
    }
}