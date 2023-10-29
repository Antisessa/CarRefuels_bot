package ru.antisessa.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.antisessa.service.AppUserService;
import ru.antisessa.service.ProducerService;

@RequiredArgsConstructor
@Service
public class AppUserServiceImpl implements AppUserService {
    private final ProducerService producerService;

    @Override
    public String findOneCar(Update update) {
        producerService.producerFindOneCarRequest(update);
//        int id = Integer.parseInt(s);
//
//        var optionalCar = carRepository.findById(id);
//
//        if (optionalCar.isEmpty()) {
//            return "Машина с id = " + id + " не найдена";
//        } else {
//            var transientCar = optionalCar.get();

            /* TODO исправить баг
                failed to lazily initialize a collection of role: could not initialize proxy - no Session
                почему то не подгружает связанные сущности refuel при запросе transientCar.getRefuels()
                можно исправить Fetch.Type.EAGER, но все работает в RefuelService,
                настроить поиск машины не напрямую, а через него с помощью RabbitMQ
             */
            return "Request успешно ушел в очередь, жди сигнала.";
        }
    }
