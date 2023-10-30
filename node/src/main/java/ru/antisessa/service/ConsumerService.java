package ru.antisessa.service;

import org.telegram.telegrambots.meta.api.objects.Update;
import ru.antisessa.DTO.CarDTO;

public interface ConsumerService {
    void consumeTextMessageUpdated(Update update);
    void consumeGetCarResponse(CarDTO.Response.GetCar response);
    void consumeGetCarFullInfoResponse(CarDTO.Response.GetCarFullInfo response);
}
