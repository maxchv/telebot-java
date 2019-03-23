package org.itstep.telebot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;

@SpringBootApplication
@Slf4j
public class TelebotApplication {
    public static void main(String[] args) {
        ApiContextInitializer.init();
        SpringApplication.run(TelebotApplication.class, args);
    }
}
