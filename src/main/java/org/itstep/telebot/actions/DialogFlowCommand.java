package org.itstep.telebot.actions;

import lombok.extern.slf4j.Slf4j;
import org.itstep.telebot.ai.DialogFlow;
import org.itstep.telebot.bot.ChatBot;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.inject.Inject;

@Component
@Slf4j
public class DialogFlowCommand extends Command {

    private final DialogFlow dialogFlow;

    @Inject
    public DialogFlowCommand(DialogFlow dialogFlow) {
        this.dialogFlow = dialogFlow;
    }

    @Override
    public void execute(String input, Update update, ChatBot chatBot) {
        log.info(String.format("execute: %s", input));

        dialogFlow.request(input)
                .subscribe(s -> {
                    log.info("subscribe Thread: " + Thread.currentThread().getName());
                    SendMessage message = new SendMessage() // Create a SendMessage object with mandatory fields
                            .setChatId(update.getMessage().getChatId())
                            .setText(s);
                    sendMessage(message, chatBot);
                });
    }
}
