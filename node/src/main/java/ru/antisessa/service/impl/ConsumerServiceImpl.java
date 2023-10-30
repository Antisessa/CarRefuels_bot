package ru.antisessa.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.antisessa.DTO.CarDTO;
import ru.antisessa.service.ConsumerService;
import ru.antisessa.service.MainService;
import ru.antisessa.service.ProducerService;

import static ru.antisessa.RabbitQueue.*;

@Log4j
@RequiredArgsConstructor
@Service
public class ConsumerServiceImpl implements ConsumerService {
    private final MainService mainService;
    private final ProducerService producerService;

    @Override
    @RabbitListener(queues = TEXT_MESSAGE_UPDATE)
    public void consumeTextMessageUpdated(Update update) {
        log.debug("NODE: Text message is received");
        mainService.processTextMessage(update);
    }

    @Override
    public void consumeGetCarResponse(CarDTO.Response.GetCar response) {

    }

    @Override
    @RabbitListener(queues = FIND_ONE_CAR_FULL_INFO_RESPONSE)
    public void consumeGetCarFullInfoResponse(CarDTO.Response.GetCarFullInfo response) {
        log.debug("NODE: GetCar response is received");
        producerService.produceFindOneCarFullInfoResponse(response);
    }
}
