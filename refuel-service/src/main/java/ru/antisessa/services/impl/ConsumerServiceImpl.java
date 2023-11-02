package ru.antisessa.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.antisessa.services.consumers.ConsumerService;
import ru.antisessa.services.producers.ProducerService;
import ru.antisessa.util.ControllerAdvice;
import ru.antisessa.util.car.CarNotFoundException;
import ru.antisessa.utils.ConverterDTO;

import static ru.antisessa.RabbitQueue.FIND_ONE_CAR_FULL_INFO_REQUEST;

@RequiredArgsConstructor
@Log4j
@Service
public class ConsumerServiceImpl implements ConsumerService {
    private final CarServiceImpl carService;
    private final ProducerService producerService;
    private final ConverterDTO converterDTO;
    private final ControllerAdvice controllerAdvice;

    @Override
    public void consumeFindOneCar(Update update) {

    }

    @Override
    @RabbitListener(queues = FIND_ONE_CAR_FULL_INFO_REQUEST)
    public void consumeFindOneCarFullInfo(Update update) {

        log.debug("RefuelService: Find one car request is received");

        var textUpdate = update.getMessage().getText();
        var requestId = Integer.parseInt(textUpdate);

        log.debug("RefuelService: Parsed ID of request is " + requestId);

        var foundedCarFullInfo = carService.findOneCarFullInfo(requestId);

        producerService.produceAnswerFindOneCarFullInfo(update, foundedCarFullInfo);

        }
    }
