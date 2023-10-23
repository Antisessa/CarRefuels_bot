package ru.antisessa.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.antisessa.service.UpdateProducer;
import ru.antisessa.utils.MessageUtils;

import static ru.antisessa.RabbitQueue.TEXT_MESSAGE_UPDATE;

@RequiredArgsConstructor
@Log4j
@Component
public class UpdateProcessor {
    private TelegramBot telegramBot;
    private final MessageUtils messageUtils;
    private final UpdateProducer updateProducer;

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
            distributeMessageByType(update);
        } else {
            log.error("Update without message. Update: " + update);
            setExceptionMessageView(update);
        }
    }

    // Метод разбирающий пришедший update по типам контента внутри
    private void distributeMessageByType(Update update) {
        Message message = update.getMessage();
        if (message.hasText()){
            processTextMessage(update);
        } else {
            setUnsupportedMessageTypeView(update);
        }
    }

    private void setUnsupportedMessageTypeView(Update update) {
        var sendMessage = messageUtils.generateSendMessageWithText(update,
                "Неподдерживаемый тип сообщения, воспользуйтесь /help");
        setView(sendMessage);
    }

    private void setExceptionMessageView(Update update) {
        SendMessage sendMessage = messageUtils.generateSendMessageWithText(update,
                "Произошла ошибка на стороне сервера, извините за неудобства\n" +
                        "Сообщение об ошибке отправлено администратору");
        setView(sendMessage);
    }

    // proxy метод для отправки сообщений
    public void setView(SendMessage sendMessage) {
        telegramBot.sendAnswerMessage(sendMessage);
    }

    // Метод помещает update с текстовым сообщением в очередь
    private void processTextMessage(Update update) {
        updateProducer.produce(TEXT_MESSAGE_UPDATE, update);
    }
}
