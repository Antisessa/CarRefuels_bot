package ru.antisessa.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.antisessa.DTO.CarDTO;

import static ru.antisessa.RabbitQueue.*;

@Log4j
@RequiredArgsConstructor
@Service
public class ConsumerService {
    private final MainService mainService;
    private final ProducerService producerService;

    String serviceName = "NODE ConsumerService: ";

    @RabbitListener(queues = TEXT_MESSAGE_UPDATE)
    public void consumeTextMessageUpdated(Update update) {
        log.debug(serviceName + "Text message is received");
        mainService.processTextMessage(update);
    }

    @RabbitListener(queues = FIND_ONE_CAR_RESPONSE)
    public void consumeGetCarResponse(CarDTO.Response.GetCar response) {
        log.debug(serviceName + "GetCar response is received");
        producerService.produceFindOneCarResponse(response);
    }

    @RabbitListener(queues = FIND_ONE_CAR_FULL_INFO_RESPONSE)
    public void consumeGetCarFullInfoResponse(CarDTO.Response.GetCarFullInfo response) {
        log.debug(serviceName + "GetCarFullInfo response is received");
        producerService.produceFindOneCarFullInfoResponse(response);
    }
}
