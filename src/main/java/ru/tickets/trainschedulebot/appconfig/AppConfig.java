package ru.tickets.trainschedulebot.appconfig;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import ru.tickets.trainschedulebot.botApi.TelegramBot;
import ru.tickets.trainschedulebot.botApi.TelegramFacade;

@Configuration
public class AppConfig {

    private final TelegramFacade telegramFacade;

    private String webHookPath;
    private String botUserName;
    private String botToken;

    private DefaultBotOptions.ProxyType proxyType;
    private String proxyHost;
    private int proxyPort;

    public AppConfig(@Lazy TelegramFacade telegramFacade) {
        this.telegramFacade = telegramFacade;
    }


    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource
                = new ReloadableResourceBundleMessageSource();

        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    @Bean
    public TelegramBot MySuperTelegramBot() {
        DefaultBotOptions options = new DefaultBotOptions();

        // Установка параметров прокси, если они необходимы
        if (proxyHost != null && proxyPort != 0 && proxyType != null) {
            DefaultBotOptions.ProxyType proxyTypeEnum = DefaultBotOptions.ProxyType.valueOf(String.valueOf(proxyType));
            options.setProxyHost(proxyHost);
            options.setProxyPort(proxyPort);
            options.setProxyType(proxyTypeEnum);
        }

        TelegramBot mySuperTelegramBot = new TelegramBot(options, telegramFacade);
        mySuperTelegramBot.setBotUsername(botUserName);
        mySuperTelegramBot.setBotToken(botToken);
        mySuperTelegramBot.setBotPath(webHookPath);

        return mySuperTelegramBot;
    }
}
