package org.itstep.telebot.bot;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.itstep.telebot.actions.Command;
import org.itstep.telebot.actions.DialogFlowCommand;
import org.itstep.telebot.actions.StartCommand;
import org.itstep.telebot.ai.DialogFlow;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@Slf4j
public class ChatBot extends TelegramLongPollingBot {

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.bot.user_name}")
    private String botUsername;

    private final Map<String, Command> commands;
    private final DialogFlowCommand dialogFlowCommand;

    private final ExecutorService executorService;

    @Inject
    public ChatBot(DialogFlowCommand dialogFlowCommand, StartCommand startCommand) {
        this.commands = new HashMap<>();
        this.commands.put(StartCommand.CMD, startCommand);
        this.dialogFlowCommand = dialogFlowCommand;
        this.executorService = Executors.newCachedThreadPool();
    }

    @Override
    public void onUpdateReceived(Update update) {
        log.info("onUpdateReceived: " + update);
        if (update.hasMessage() && update.getMessage().hasText()) {
            log.info("message: " + update.getMessage());

            String text = update.getMessage().getText().trim();
            executorService.submit(() -> {
                Command command = commands.getOrDefault(text, dialogFlowCommand);
                command.execute(text, update, this);
            });
        }
        if (update.hasCallbackQuery()) {
            val callbackQuery = update.getCallbackQuery();
            String data = callbackQuery.getData().trim();
            log.info("data: " + data);
            AnswerCallbackQuery answer = new AnswerCallbackQuery();
            answer.setCallbackQueryId(callbackQuery.getId());
            answer.setText(data);
            //answer.setShowAlert(true);
            executorService.submit(() -> {
                try {
                    execute(answer);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    @Override
    public void onClosing() {
        super.onClosing();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdownNow();
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
