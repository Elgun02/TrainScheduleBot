package ru.tickets.trainschedulebot.botApi;

import lombok.AccessLevel;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Telegram bot implementation extending TelegramWebhookBot.
 * Handles incoming updates from the Telegram server and delegates processing to the TelegramFacade.
 *
 * @author Elgun
 */
@Slf4j
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TelegramBot extends TelegramWebhookBot {
    private final TelegramFacade telegramFacade;

    private String botUsername;
    private String botPath;

    /**
     * Constructor to initialize the Telegram bot with custom options, token, and a TelegramFacade instance.
     *
     * @param options        Custom bot options.
     * @param token          The Telegram bot token.
     * @param telegramFacade The facade responsible for handling Telegram updates.
     */
    public TelegramBot(DefaultBotOptions options, String token, TelegramFacade telegramFacade) {
        super(options, token);
        this.telegramFacade = telegramFacade;
    }

    /**
     * Handles incoming updates from the Telegram server and delegates processing to the TelegramFacade.
     *
     * @param update The incoming update from the Telegram server.
     * @return BotApiMethod representing the response to be sent back to the user or the chat.
     */
    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        return telegramFacade.handleUpdate(update);
    }

    /**
     * Gets the bot webhook path as configured in the Telegram API.
     *
     * @return The bot webhook path.
     */
    @Override
    public String getBotPath() {
        return botPath;
    }

    /**
     * Gets the bot username as configured in the Telegram API.
     *
     * @return The bot username.
     */
    @Override
    public String getBotUsername() {
        return botUsername;
    }
}