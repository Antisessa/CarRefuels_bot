package ru.antisessa.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.hibernate.LazyInitializationException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.antisessa.DTO.CarDTO;
import ru.antisessa.models.Car;
import ru.antisessa.services.CarService;
import ru.antisessa.services.consumers.ConsumerService;
import ru.antisessa.services.producers.ProducerService;
import ru.antisessa.util.car.CarNotFoundException;
import ru.antisessa.utils.ConverterDTO;

import static ru.antisessa.RabbitQueue.FIND_ONE_CAR_FULL_INFO_REQUEST;
import static ru.antisessa.RabbitQueue.FIND_ONE_CAR_REQUEST;

@RequiredArgsConstructor
@Log4j
@Service
public class ConsumerServiceImpl implements ConsumerService {
    private final CarServiceImpl carService;
    private final ProducerService producerService;
    private final ConverterDTO converterDTO;

    @Override
    public void consumeFindOneCar(Update update) {
        //TODO закончить метод
    }

    @Override
    @RabbitListener(queues = FIND_ONE_CAR_FULL_INFO_REQUEST)
    public void consumeFindOneCarFullInfo(Update update) {
        log.debug("RefuelService: Find one car request is received");

        CarDTO.Response.GetCarFullInfo output = null;

        try {

        var textUpdate = update.getMessage().getText();
        var requestId = Integer.parseInt(textUpdate);

        log.debug("RefuelService: Parsed ID of request is " + requestId);

        var carDTO = carService.findOneTest(requestId);

        output = carDTO;

        } catch (CarNotFoundException e) {
            log.debug(e.getMessage());
        } catch (LazyInitializationException e) {
            log.fatal(e.getMessage());
        }
        finally {
            producerService.produceAnswerFindOneCarFullInfo(update, output);
        }

    }
}
