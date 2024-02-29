package ru.tickets.trainschedulebot.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.tickets.trainschedulebot.botApi.TelegramBot;

/**
 * The {@code WebHookController} class is a Spring MVC controller that handles incoming webhook updates
 * from the Telegram Bot API. It forwards the received {@link Update} to the configured {@link TelegramBot}
 * for processing and returns the corresponding {@link BotApiMethod} as a response.
 * <p>
 * This class is annotated with {@link RestController} to indicate that it handles REST ful requests, and
 * it is responsible for defining the endpoint ("/") for receiving webhook updates.
 * <p>
 * The {@link RequestMapping} annotation specifies that this controller method is triggered for HTTP POST
 * requests to the root ("/") path.
 *
 * @author Elgun Dilanchiev
 * @version 1.0
 * @since 2024-02-28
 */
@Slf4j
@RestController
public class WebHookController {
    private final TelegramBot telegramBot;

    /**
     * Constructs a new {@code WebHookController} with the provided {@link TelegramBot}.
     *
     * @param telegramBot The Telegram bot instance to be used for processing updates.
     */
    public WebHookController(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    /**
     * Handles incoming webhook updates from the Telegram Bot API.
     *
     * @param update The {@link Update} object containing the incoming update data.
     * @return The {@link BotApiMethod} representing the response to be sent to Telegram.
     */
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public BotApiMethod<?> onUpdateReceived(@RequestBody Update update) {
        return telegramBot.onWebhookUpdateReceived(update);
    }
}