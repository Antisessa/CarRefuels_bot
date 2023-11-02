package ru.antisessa.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.antisessa.controller.UpdateProcessor;
import ru.antisessa.service.ConsumerService;

import static ru.antisessa.RabbitQueue.ANSWER_MESSAGE;
import static ru.antisessa.RabbitQueue.ANSWER_MESSAGE_WITH_EXCEPTION;

@Log4j
@RequiredArgsConstructor
@Service
public class ConsumerServiceImpl implements ConsumerService {
    private final UpdateProcessor updateProcessor;

    // Метод обрабатывает очередь ANSWER_MESSAGE и передает их контроллеру в метод
    @Override
    @RabbitListener(queues = ANSWER_MESSAGE)
    public void consumeAnswerMessage(SendMessage sendMessage) {
        updateProcessor.setView(sendMessage);
    }

    @Override
    @RabbitListener(queues = ANSWER_MESSAGE_WITH_EXCEPTION)
    public void consumeAnswerMessageWithException(SendMessage sendMessage) {
        updateProcessor.setView(sendMessage);
    }
}
