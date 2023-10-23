package ru.antisessa.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.antisessa.utils.MessageUtils;

@RequiredArgsConstructor
@Log4j
@Component
public class UpdateProcessor {
    private TelegramBot telegramBot;
    private final MessageUtils messageUtils;

    public void registerBot(TelegramBot telegramBot) {
        System.out.println("Class UpdateProcessor created");
        this.telegramBot = telegramBot;
    }

    public void processUpdate(Update update) {
        // Если обновление пустое зарегистрировать лог с ошибкой
        if (update == null) {
            log.error("Received update is null");
            return;
        }

        if(update.hasMessage()){
            // TODO заглушка
            var sendMessage = messageUtils.generateSendMessageWithText(update,
                    "Хэллоу энд велком ту лос полос харманос фемели..");
            setView(sendMessage);
        }
    }

    private void setUnsupportedMessageTypeView(Update update) {
        var sendMessage = messageUtils.generateSendMessageWithText(update,
                "Неподдерживаемый тип сообщения!");
        setView(sendMessage);
    }

    // proxy метод для отправки сообщений
    public void setView(SendMessage sendMessage) {
        telegramBot.sendAnswerMessage(sendMessage);
    }
}
