package ru.tickets.trainschedulebot.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
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

/**
 * The {@code BotConfig} class is a Spring configuration class responsible for creating and configuring
 * beans related to the Telegram Bot. It utilizes the Lombok annotations for simplified code structure
 * and configuration properties to externalize the bot-related settings.
 * <p>
 * This class configures and initializes the {@link TelegramBot} bean using the provided Telegram
 * Bot token, username, and webhook settings. Additionally, it sets up a proxy for the bot if specified
 * in the configuration properties.
 * <p>
 * The bot webhook is also set using the {@link SetWebhook} method during bean initialization.
 * <p>
 * This class is annotated with {@link Configuration}, {@link Component}, and {@link ConfigurationProperties}
 * to indicate that it contains configuration methods, is a Spring component, and has externalized configuration
 * properties, respectively.
 *
 * @see TelegramBot
 * @see TelegramFacade
 * @see DefaultBotOptions
 * @see SetWebhook
 */
@Slf4j
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

    /**
     * Configures and creates a {@link TelegramBot} bean, which represents the Telegram bot used in the application.
     * The bot is configured with the provided properties such as webhook path, username, token, and optional proxy settings.
     * It also sets the webhook for the bot during initialization.
     * <p>
     * This method is annotated with {@link Bean} to indicate that it produces a bean to be managed by the Spring framework.
     *
     * @param telegramFacade The {@link TelegramFacade} used to handle Telegram updates and events.
     * @return The configured {@link TelegramBot} bean.
     */
    @Bean
    public TelegramBot telegramBot(@Lazy TelegramFacade telegramFacade) {
        // Create and configure the options for the Telegram bot
        DefaultBotOptions options = new DefaultBotOptions();
        DefaultBotOptions.ProxyType proxyTypeEnum = DefaultBotOptions.ProxyType.valueOf(String.valueOf(proxyType));
        options.setProxyHost(proxyHost);
        options.setProxyPort(proxyPort);
        options.setProxyType(proxyTypeEnum);

        // Create the Telegram bot instance with the configured options, token, and TelegramFacade
        TelegramBot myTelegramBot = new TelegramBot(options, botToken, telegramFacade);
        myTelegramBot.setBotUsername(userName);
        myTelegramBot.setBotPath(webHookPath);

        // Set the webhook for the bot
        SetWebhook setWebhook = new SetWebhook(webHookPath);

        try {
            log.info("Executing setWebhook: {}", setWebhook);
            myTelegramBot.execute(setWebhook);
            log.info("Webhook successfully set");
        } catch (TelegramApiException e) {
            log.error("Error executing setWebhook: {}", setWebhook, e);
        }

        return myTelegramBot;
    }

}
