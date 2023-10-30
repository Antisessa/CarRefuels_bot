package ru.antisessa.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.antisessa.DTO.CarDTO;
import ru.antisessa.DTO.RefuelDTO;
import ru.antisessa.service.ProducerService;

import static ru.antisessa.RabbitQueue.*;

@Log4j
@RequiredArgsConstructor
@Service
public class ProducerServiceImpl implements ProducerService {
    private final RabbitTemplate rabbitTemplate;

    //Метод передает в очередь RabbitMQ_ANSWER_MESSAGE сообщение для пользователя
    @Override
    public void produceAnswer(SendMessage sendMessage) {
        log.debug("NODE: Answer message is produced");
        rabbitTemplate.convertAndSend(ANSWER_MESSAGE, sendMessage);
    }

    //Метод передает в очередь RabbitMQ FIND_ONE_REQUEST id для поиска машины
    @Override
    public void produceFindOneCarFullInfoRequest(Update update) {
        log.debug("NODE: Find one car full info request is produced");
        rabbitTemplate.convertAndSend(FIND_ONE_CAR_FULL_INFO_REQUEST, update);
    }

    @Override
    public void produceFindOneCarResponse(CarDTO.Response.GetCar response) {
        log.debug("NODE: Starting process find one car response...");

        var sb = new StringBuilder();

        sb.append(response.getName()).append(", ")
                .append("с пробегом: ").append(response.getOdometer()).append(",\n")
                .append("количество записанных заправок: ").append(response.getCountRefuels());


        // name, с пробегом: + odometer, количество записанных заправок = countRefuels
        var text = sb.toString();

        log.debug("NODE: Find one car response is processed");

        var update = response.getUpdate();
        var sendMessage = new SendMessage();
        sendMessage.setChatId(update.getMessage().getChatId());
        sendMessage.setText(text);

        log.debug("NODE: Answer message is produced");
        rabbitTemplate.convertAndSend(ANSWER_MESSAGE, sendMessage);
    }

    @Override
    public void produceFindOneCarFullInfoResponse(CarDTO.Response.GetCarFullInfo response) {
        log.debug("NODE: Starting process find one car full info response...");

        var sb = new StringBuilder();

        sb.append(response.getName()).append(", ")
                .append("с пробегом: ").append(response.getOdometer()).append(",\n")
                .append("количество записанных заправок: ")
                .append(response.getCountRefuels()).append("\n");

        int index = 0;

        for (RefuelDTO.Response.GetRefuel refuel : response.getRefuels()) {
            index++;
            sb.append("Заправка № ").append(index).append(",\n")
                    .append("Дата и время заправки: ").append(refuel.getDateTime()).append(",\n")
                    .append("заправленный объем: ").append(refuel.getVolume()).append(",\n")
                    .append("стоимость заправки: ").append(refuel.getCost()).append(",\n")
                    .append("Рассчитанный расход на момент заправки: ").append(refuel.getCalculatedConsumption()).append(",\n\n");

        }

            var text = sb.toString();

            log.debug("NODE: Find one car response is processed");

            var update = response.getUpdate();
            var sendMessage = new SendMessage();
            sendMessage.setChatId(update.getMessage().getChatId());
            sendMessage.setText(text);

            log.debug("NODE: Answer message is produced");
            rabbitTemplate.convertAndSend(ANSWER_MESSAGE, sendMessage);
        }
    }
