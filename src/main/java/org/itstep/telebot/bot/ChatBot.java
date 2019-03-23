package org.itstep.telebot.bot;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@Slf4j
public class ChatBot extends TelegramLongPollingBot {

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.bot.user_name}")
    private String botUsername;

    @Override
    public void onUpdateReceived(Update update) {
        log.info("Receive message: " + update.getMessage());
        if (update.hasMessage() && update.getMessage().hasText()) {
            val msg = update.getMessage();
            SendMessage message = new SendMessage()
                    .setChatId(msg.getChatId())
                    .setText(msg.getText());
            try {
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}
