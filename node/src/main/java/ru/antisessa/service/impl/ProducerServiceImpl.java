package ru.antisessa.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.antisessa.service.ProducerService;

import static ru.antisessa.RabbitQueue.ANSWER_MESSAGE;
import static ru.antisessa.RabbitQueue.FIND_ONE_CAR_REQUEST;

@Log4j
@RequiredArgsConstructor
@Service
public class ProducerServiceImpl implements ProducerService {
    private final RabbitTemplate rabbitTemplate;

    //Метод передает в очередь RabbitMQ_ANSWER_MESSAGE сообщение для пользователя
    @Override
    public void producerAnswer(SendMessage sendMessage) {
        log.debug("NODE: Answer message is produced");
        rabbitTemplate.convertAndSend(ANSWER_MESSAGE, sendMessage);
    }

    //Метод передает в очередь RabbitMQ FIND_ONE_REQUEST id для поиска машины
    @Override
    public void producerFindOneCarRequest(Update update) {
        log.debug("NODE: Find one car request is produced");
        rabbitTemplate.convertAndSend(FIND_ONE_CAR_REQUEST, update);
    }
}
