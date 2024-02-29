package ru.tickets.trainschedulebot.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.client.RestTemplate;

/**
 * The {@code AppConfig} class is a Spring configuration class that defines and configures beans
 * for the application. It includes methods to create and customize beans such as {@link RestTemplate}
 * for making HTTP requests and {@link MessageSource} for handling message localization.
 * <p>
 * This class is annotated with {@link Configuration} to indicate that it contains configuration
 * methods for creating and customizing beans.
 *
 * @see RestTemplate
 * @see MessageSource
 * @see RestTemplateBuilder
 * @see ReloadableResourceBundleMessageSource
 *
 * @author Elgun Dilanchiev
 * @version 1.0
 * @since 2024-02-28
 */
@Configuration
public class AppConfig {

    /**
     * Creates and configures a {@link RestTemplate} bean for making HTTP requests.
     *
     * @param builder The {@link RestTemplateBuilder} used to build the {@link RestTemplate}.
     * @return The configured {@link RestTemplate} bean.
     */
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    /**
     * Creates and configures a {@link MessageSource} bean for handling message localization.
     *
     * @return The configured {@link MessageSource} bean.
     */
    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource
                = new ReloadableResourceBundleMessageSource();

        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
}