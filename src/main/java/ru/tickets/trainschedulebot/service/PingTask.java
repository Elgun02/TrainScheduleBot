package ru.tickets.trainschedulebot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Special app invoker for Heroku free plan.
 * Provides app not to sleep after 30 min inactive.
 *
 * @author Elgun Dilanchiev
 */
@Service
public class PingTask {

    public static final Logger logger = LoggerFactory.getLogger(PingTask.class);

    @Value("${ping.task.url}")
    private String url;

    @Scheduled(fixedRateString = "${ping.task.period}")
    public void pingMe() {
        try {
            URL url = new URL(getUrl());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            logger.info("Ping {}, OK: response code {}", url.getHost(), connection.getResponseCode());
            connection.disconnect();
        } catch (IOException e) {
            logger.error("Ping Failed: " + e.getMessage());
        }
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}