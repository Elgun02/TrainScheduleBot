package ru.tickets.trainschedulebot.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.tickets.trainschedulebot.botApi.TelegramBot;
import ru.tickets.trainschedulebot.botApi.TelegramFacade;

@Getter
@Setter
@Component
@Configuration
@FieldDefaults(level = AccessLevel.PRIVATE)
@ConfigurationProperties(prefix = "telegrambot")
public class BotConfig {
    private String webHookPath;
    private String userName;
    private String botToken;

    private DefaultBotOptions.ProxyType proxyType;
    private String proxyHost;
    private int proxyPort;

    @Bean
    public TelegramBot myTelegramBot(@Lazy TelegramFacade telegramFacade) {
        DefaultBotOptions options = new DefaultBotOptions();

        DefaultBotOptions.ProxyType proxyTypeEnum = DefaultBotOptions.ProxyType.valueOf(String.valueOf(proxyType));
        options.setProxyHost(proxyHost);
        options.setProxyPort(proxyPort);
        options.setProxyType(proxyTypeEnum);

        TelegramBot mySuperTelegramBot = new TelegramBot(options, telegramFacade);
        mySuperTelegramBot.setBotUsername(userName);
        mySuperTelegramBot.setBotPath(webHookPath);

        return mySuperTelegramBot;
    }
}
