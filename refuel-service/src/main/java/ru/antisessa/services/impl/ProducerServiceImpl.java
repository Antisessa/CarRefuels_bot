package ru.antisessa.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.antisessa.models.Car;
import ru.antisessa.services.producers.ProducerService;

import static ru.antisessa.RabbitQueue.ANSWER_MESSAGE;
import static ru.antisessa.RabbitQueue.FIND_ONE_CAR_RESPONSE;

@RequiredArgsConstructor
@Log4j
@Service
public class ProducerServiceImpl implements ProducerService {
    private final RabbitTemplate rabbitTemplate;

    @Override
    public void produceAnswerFindOne(Update update, String answer) {
        var chatId = update.getMessage().getChatId();

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(answer);

        rabbitTemplate.convertAndSend(ANSWER_MESSAGE, sendMessage);
        log.debug("RefuelService: Find one car response is produced");
    }
}

