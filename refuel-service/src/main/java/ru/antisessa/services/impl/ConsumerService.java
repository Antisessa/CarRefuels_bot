package ru.antisessa.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.antisessa.services.CarService;
import ru.antisessa.util.ControllerAdvice;
import ru.antisessa.utils.ConverterDTO;

import static ru.antisessa.RabbitQueue.FIND_ONE_CAR_FULL_INFO_REQUEST;
import static ru.antisessa.RabbitQueue.FIND_ONE_CAR_REQUEST;

@RequiredArgsConstructor
@Log4j
@Service
public class ConsumerService {
    private final CarService carService;
    private final ProducerService producerService;
    private final ConverterDTO converterDTO;
    private final ControllerAdvice controllerAdvice;

    @RabbitListener(queues = FIND_ONE_CAR_REQUEST)
    public void consumeFindOneCarRequest(Update update) {
        log.debug("RefuelService Consumer: Find one car full info request is received");

        var textUpdate = update.getMessage().getText();
        var requestId = Integer.parseInt(textUpdate);

        log.debug("RefuelService Consumer: Parsed ID of request is " + requestId);

        var foundedCar = carService.findOneCar(requestId);

        producerService.produceAnswerFindOneCar(update, foundedCar);

    }

    @RabbitListener(queues = FIND_ONE_CAR_FULL_INFO_REQUEST)
    public void consumeFindOneCarFullInfoRequest(Update update) {
        log.debug("RefuelService Consumer: Find one car full info request is received");

        var textUpdate = update.getMessage().getText();
        var requestId = Integer.parseInt(textUpdate);

        log.debug("RefuelService Consumer: Parsed ID of request is " + requestId);

        var foundedCarFullInfo = carService.findOneCarFullInfo(requestId);

        producerService.produceAnswerFindOneCarFullInfo(update, foundedCarFullInfo);

        }
    }
