package ru.antisessa.utils;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class MessageUtils {
    // Метод для генерации ответного сообщения, в аргументах - обновление и текст для сообщения
    public SendMessage generateSendMessageWithText(Update update, String text) {

        // Достаем оригинальное входящее сообщение из обновления
        var message = update.getMessage();

        // Создаем экземпляр исходящего сообщения
        var sendMessage = new SendMessage();

        // Присваиваем исходящему сообщению нужный Chat ID
        sendMessage.setChatId(message.getChatId().toString());

        // Присваиваем ему текст сообщения
        sendMessage.setText(text);

        return sendMessage;
    }
}
