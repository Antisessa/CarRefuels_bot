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

import java.time.format.DateTimeFormatter;

import static ru.antisessa.RabbitQueue.*;

@Log4j
@RequiredArgsConstructor
@Service
public class ProducerServiceImpl implements ProducerService {
    private final RabbitTemplate rabbitTemplate;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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

        var sendMessage = prepareSendMessage(text, response.getUpdate());

        log.debug("NODE: Answer message is produced");
        rabbitTemplate.convertAndSend(ANSWER_MESSAGE, sendMessage);
    }

    @Override
    public void produceFindOneCarFullInfoResponse(CarDTO.Response.GetCarFullInfo response) {
        log.debug("NODE: Starting process find one car full info response...");

        if(response.getUpdate() == null) {
            log.error("NODE: Получено сообщение с update == null");
            return;
        }

        if(response.getName() == null) {
            var update = response.getUpdate();
            var requestId = response.getUpdate().getMessage().getText();
            var text = "Машина с id = " + requestId + " не найдена.";

            var sendMessage = prepareSendMessage(text, update);

            rabbitTemplate.convertAndSend(ANSWER_MESSAGE, sendMessage);
            return;
        }

        var sb = new StringBuilder();
                sb.append("Идентификатор машины: ").append(response.getName()).append(", id = ")
                .append(response.getId()).append(",\n")
                .append("Текущий записанный пробег: ").append(response.getOdometer()).append(",\n\n");

        int index = 0; // счетчик для forEach по заправкам

        for (RefuelDTO.Response.GetRefuelFullInfo refuel : response.getRefuels()) {
            index++;
            sb.append("Заправка № ").append(index).append(",\n")
                    .append("Дата и время заправки: ").append(refuel.getDateTime().format(formatter)).append(",\n")
                    .append("Показания одометра на момент заправки: ").append(refuel.getOdometerRecord()).append(",\n")

                    .append("Пройденное расстояние с последней заправки: ")
                    .append(refuel.getOdometerRecord()-refuel.getPreviousOdometerRecord()).append(",\n")

                    .append("Заправленный объем: ").append(refuel.getVolume()).append(",\n")
                    .append("Стоимость заправки: ").append(refuel.getCost()).append(",\n")

                    .append("Рассчитанный расход на момент заправки: ")
                    .append(refuel.getCalculatedConsumption()).append(",\n\n");

        }
            var text = sb.toString();
            var sendMessage = prepareSendMessage(text, response.getUpdate());

            log.debug("NODE: Answer message is produced");
            rabbitTemplate.convertAndSend(ANSWER_MESSAGE, sendMessage);
        }

        @Override
        public SendMessage prepareSendMessage(String message, Update update){
            var sendMessage = new SendMessage();
            sendMessage.setChatId(update.getMessage().getChatId());
            sendMessage.setText(message);
            return sendMessage;
        }
    }
