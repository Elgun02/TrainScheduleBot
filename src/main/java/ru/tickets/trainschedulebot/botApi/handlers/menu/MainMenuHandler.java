package ru.tickets.trainschedulebot.botApi.handlers.menu;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.tickets.trainschedulebot.botApi.handlers.state.BotState;
import ru.tickets.trainschedulebot.botApi.handlers.InputMessageHandler;
import ru.tickets.trainschedulebot.service.MainMenuService;
import ru.tickets.trainschedulebot.service.ReplyMessagesService;

/**
 * Handles user requests for main menu.
 *
 * @author Elgun Dilanchiev
 * @version 1.0
 * @since 2024-02-29
 */
@Component
@RequiredArgsConstructor
public class MainMenuHandler implements InputMessageHandler {
    private final ReplyMessagesService messagesService;
    private final MainMenuService mainMenuService;

    /**
     * Handles user's message requesting main menu.
     *
     * @param message The Telegram message received from the user.
     * @return SendMessage object with the response to the main menu request.
     */
    @Override
    public SendMessage handle(Message message) {
        return mainMenuService.getMainMenuMessage(message.getChatId(), messagesService.getReplyText("reply.mainMenu.welcomeMessage"));
    }

    /**
     * Gets the handler's name representing the state.
     *
     * @return BotState representing the handler's name.
     */
    @Override
    public BotState getHandlerName() {
        return BotState.SHOW_MAIN_MENU;
    }
}
