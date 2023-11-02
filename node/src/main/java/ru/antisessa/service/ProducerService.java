package ru.antisessa.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.antisessa.DTO.CarDTO;

public interface ProducerService {
    void produceAnswer(SendMessage sendMessage);

    void produceFindOneCarFullInfoRequest(Update update);

    void produceFindOneCarResponse(CarDTO.Response.GetCar response);

    void produceFindOneCarFullInfoResponse(CarDTO.Response.GetCarFullInfo response);

    SendMessage prepareSendMessage(String message, Update update);
}
