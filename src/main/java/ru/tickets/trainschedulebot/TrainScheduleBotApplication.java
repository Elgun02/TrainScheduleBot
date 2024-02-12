package ru.tickets.trainschedulebot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TrainScheduleBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(TrainScheduleBotApplication.class, args);
    }

}
