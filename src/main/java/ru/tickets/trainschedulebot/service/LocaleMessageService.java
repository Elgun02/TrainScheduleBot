package ru.tickets.trainschedulebot.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Locale;

/**
 * Service class for retrieving localized messages using a specified locale.
 * This class interacts with a {@link MessageSource} to retrieve messages based on the provided locale.
 *
 * @author Elgun Dilanchiev
 * @version 1.0
 * @since 2024-02-28
 */
@Service
public class LocaleMessageService {
    private final Locale locale;
    private final MessageSource messageSource;

    /**
     * Constructs a new LocaleMessageService with the specified locale tag and MessageSource.
     *
     * @param localeTag       The language tag for the desired locale.
     * @param messageSource   The MessageSource used for retrieving messages.
     */
    public LocaleMessageService(@Value("${localeTag}") String localeTag, @Lazy MessageSource messageSource) {
        this.locale = Locale.forLanguageTag(localeTag);
        this.messageSource = messageSource;
    }

    /**
     * Retrieves a localized message for the given message code.
     *
     * @param message The code for the desired message.
     * @return The localized message.
     */
    public String getMessage(String message) {
        return messageSource.getMessage(message, null, locale);
    }

    /**
     * Retrieves a localized message for the given message code with parameterized arguments.
     *
     * @param message The code for the desired message.
     * @param args    The arguments to be replaced in the message placeholders.
     * @return The localized message with substituted arguments.
     */
    public String getMessage(String message, Object... args) {
        return messageSource.getMessage(message, args, locale);
    }
}