package ru.antisessa.services.producers;

import org.telegram.telegrambots.meta.api.objects.Update;
import ru.antisessa.DTO.CarDTO;

public interface ProducerService {
    void produceAnswerFindOne(Update update, CarDTO.Response.GetCar answer);
    void produceAnswerFindOneCarFullInfo(Update update, CarDTO.Response.GetCarFullInfo answer);
}
