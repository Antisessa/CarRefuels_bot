package ru.antisessa.utils;

import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

public class MessageUtils {
    // Метод для генерации ответного сообщения, в аргументах - обновление и текст для сообщения
    public SendMessage generateSendMessageWithText(Update update, String text) {
        // Создаем экземпляр исходящего сообщения
        var sendMessage = new SendMessage();

        Message message = update.getMessage();

        Long chatId = message.getChatId();

        // Присваиваем исходящему сообщению нужный Chat ID
        sendMessage.setChatId(chatId.toString());

        // Присваиваем ему текст сообщения
        sendMessage.setText(text);

        return sendMessage;
    }
}
