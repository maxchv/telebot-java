package org.itstep.telebot.actions;

import lombok.extern.slf4j.Slf4j;
import org.itstep.telebot.bot.ChatBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
public abstract class Command {

    public abstract void execute(String input, Update update, ChatBot chatBot);

    public void sendMessage(SendMessage msg, ChatBot chatBot)
    {
        try {
            chatBot.execute(msg); // Call method to send the message
        } catch (TelegramApiException e) {
            log.error("", e);
        }
    }
}
