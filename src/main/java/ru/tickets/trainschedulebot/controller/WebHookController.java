package ru.tickets.trainschedulebot.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.tickets.trainschedulebot.botApi.TelegramBot;
import ru.tickets.trainschedulebot.model.UserTicketsSubscription;
import ru.tickets.trainschedulebot.service.UserTicketsSubscriptionService;

import java.util.List;

@Slf4j
@RestController
public class WebHookController {
    private final TelegramBot telegramBot;
    private final UserTicketsSubscriptionService subscriptionService;

    public WebHookController(TelegramBot telegramBot, UserTicketsSubscriptionService subscriptionService) {
        this.telegramBot = telegramBot;
        this.subscriptionService = subscriptionService;
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public BotApiMethod<?> onUpdateReceived(@RequestBody Update update) {
        return telegramBot.onWebhookUpdateReceived(update);
    }

    @GetMapping(value = "/subscriptions", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<UserTicketsSubscription> index() {
        return subscriptionService.getAllSubscriptions();
    }
}
