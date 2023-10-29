package ru.antisessa.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.antisessa.controller.UpdateProcessor;
import ru.antisessa.service.AnswerConsumer;

import static ru.antisessa.RabbitQueue.ANSWER_MESSAGE;

@Log4j
@RequiredArgsConstructor
@Service
public class AnswerConsumerImpl implements AnswerConsumer {
    private final UpdateProcessor updateProcessor;

    // Метод обрабатывает очередь ANSWER_MESSAGE и передает их контроллеру в метод
    @Override
    @RabbitListener(queues = ANSWER_MESSAGE)
    public void consume(SendMessage sendMessage) {
        if (sendMessage.getChatId() == null) {
            log.fatal("Dispatcher: sendMessage chatID is null");
        } else {
            updateProcessor.setView(sendMessage);
        }
    }
}
