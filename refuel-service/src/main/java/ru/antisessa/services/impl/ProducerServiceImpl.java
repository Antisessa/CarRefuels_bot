package ru.antisessa.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.antisessa.DTO.CarDTO;
import ru.antisessa.models.Car;
import ru.antisessa.services.producers.ProducerService;
import ru.antisessa.util.CustomCarRefuelException;

import static ru.antisessa.RabbitQueue.*;

@RequiredArgsConstructor
@Log4j
@Service
public class ProducerServiceImpl implements ProducerService {
    private final RabbitTemplate rabbitTemplate;

    @Override
    public void produceAnswerFindOne(Update update, CarDTO.Response.GetCar answer) {
        //TODO закончить метод
    }

    @Override
    public void produceAnswerFindOneCarFullInfo(Update update, CarDTO.Response.GetCarFullInfo answer) {
        answer.setUpdate(update);

        rabbitTemplate.convertAndSend(FIND_ONE_CAR_FULL_INFO_RESPONSE, answer);
        log.debug("RefuelService: Find one car response is produced");
    }
}

