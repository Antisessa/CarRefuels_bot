package ru.antisessa.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.antisessa.DTO.CarDTO;

import static ru.antisessa.RabbitQueue.*;

@RequiredArgsConstructor
@Log4j
@Service
public class ProducerService {
    private final RabbitTemplate rabbitTemplate;

    public void produceAnswerFindOneCar(Update update, CarDTO.Response.GetCar answer) {
        answer.setUpdate(update);
        rabbitTemplate.convertAndSend(FIND_ONE_CAR_RESPONSE, answer);
        log.debug("RefuelService Producer: Find one car response is produced");
    }

    public void produceAnswerFindOneCarFullInfo(Update update, CarDTO.Response.GetCarFullInfo answer) {
        answer.setUpdate(update);

        rabbitTemplate.convertAndSend(FIND_ONE_CAR_FULL_INFO_RESPONSE, answer);
        log.debug("RefuelService Producer: Find one car full info response is produced");
    }
}

