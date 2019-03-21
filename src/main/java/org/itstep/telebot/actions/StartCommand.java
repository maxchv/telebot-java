package org.itstep.telebot.actions;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.itstep.telebot.bot.ChatBot;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
@Slf4j
public class StartCommand extends Command {

    public static final String CMD = "/start";

    @Override
    public void execute(String input, Update update, ChatBot chatBot) {
        SendMessage message = new SendMessage() // Create a SendMessage object with mandatory fields
                .setChatId(update.getMessage().getChatId())
                .enableHtml(true)
                .setText("<strong>Привет!!!</strong>. Это чат бот itstep. Чем могу быть полезен?")
//                .setReplyMarkup(replyKeyBoard());
                .setReplyMarkup(inlineKeyBoard());
        sendMessage(message, chatBot);
    }

    public ReplyKeyboardMarkup replyKeyBoard() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        // Создаем список строк клавиатуры
        List<KeyboardRow> keyboard = new ArrayList<>();

        // Первая строчка клавиатуры
        KeyboardRow keyboardFirstRow = new KeyboardRow();
        // Добавляем кнопки в первую строчку клавиатуры
        keyboardFirstRow.add(new KeyboardButton("Привет"));

        // Вторая строчка клавиатуры
        KeyboardRow keyboardSecondRow = new KeyboardRow();
        // Добавляем кнопки во вторую строчку клавиатуры
        keyboardSecondRow.add(new KeyboardButton("Помощь"));

        // Добавляем все строчки клавиатуры в список
        keyboard.add(keyboardFirstRow);
        keyboard.add(keyboardSecondRow);
        // и устанваливаем этот список нашей клавиатуре
        replyKeyboardMarkup.setKeyboard(keyboard);
        return replyKeyboardMarkup;
    }

    public InlineKeyboardMarkup inlineKeyBoard() {
        return new InlineKeyboardMarkup()
                .setKeyboard(
                        Arrays.asList(
                                Arrays.asList( // row #1
                                        new InlineKeyboardButton()
                                                .setText("Тык 1").setCallbackData("/one"),
                                        new InlineKeyboardButton()
                                                .setText("Тык 2").setCallbackData("/two")
                                ),
                                Collections.singletonList( // row #2
                                        new InlineKeyboardButton()
                                                .setText("Тык 3").setCallbackData("/three")
                                )
                        ));
    }
}
