package ru.antisessa.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.antisessa.DTO.CarDTO;
import ru.antisessa.DTO.RefuelDTO;

import java.time.format.DateTimeFormatter;

import static ru.antisessa.RabbitQueue.*;

@Log4j
@RequiredArgsConstructor
@Service
public class ProducerService {
    private final RabbitTemplate rabbitTemplate;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    //Метод передает в очередь ANSWER_MESSAGE сообщение для пользователя
    public void produceAnswer(SendMessage sendMessage) {
        log.debug("NODE ProducerService: Answer message is produced");
        rabbitTemplate.convertAndSend(ANSWER_MESSAGE, sendMessage);
    }

    //Метод передает в очередь FIND_ONE_CAR_FULL_INFO_REQUEST update с вложенным id для поиска машины
    public void produceFindOneCarFullInfoRequest(Update update) {
        log.debug("NODE ProducerService: Find one car full info request is produced");
        rabbitTemplate.convertAndSend(FIND_ONE_CAR_FULL_INFO_REQUEST, update);
    }

    //Метод передает в очередь FIND_ONE_CAR_REQUEST update с вложенным id для поиска машины
    public void produceFindOneCarRequest(Update update) {
        log.debug("NODE ProducerService: Find one car request is produced");
        rabbitTemplate.convertAndSend(FIND_ONE_CAR_REQUEST, update);
    }

    public void produceFindOneCarResponse(CarDTO.Response.GetCar response) {
        log.debug("NODE ProducerService: Starting process find one car response...");

        if(response.getUpdate() == null) {
            log.error("NODE ProducerService: Получено сообщение с update == null");
            return;
        } else if (!validateResponse(response)) {
            return;
        }

        var text = response.getName() + ", " +
                "с пробегом: " + response.getOdometer() + ",\n" +
                "количество записанных заправок: " + response.getCountRefuels();

        var sendMessage = prepareSendMessage(text, response.getUpdate());

        log.debug("NODE ProducerService: Answer message with FindOneCarResponse is produced");
        rabbitTemplate.convertAndSend(ANSWER_MESSAGE, sendMessage);
    }

    public void produceFindOneCarFullInfoResponse(CarDTO.Response.GetCarFullInfo response) {
        log.debug("NODE ProducerService: Starting process find one car full info response...");

        if(response.getUpdate() == null) {
            log.error("NODE ProducerService: Получено сообщение с update == null");
            return;
        } else if (!validateResponse(response)) {
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

            log.debug("NODE ProducerService: Answer message with FindOneCarFullInfoResponse is produced");
            rabbitTemplate.convertAndSend(ANSWER_MESSAGE, sendMessage);
        }

    public <S extends CarDTO.Response.GetCar> boolean validateResponse(S response){
        log.debug("NODE ProducerService: Starting validate " + response.getClass());

        if (response.getName() == null) {
            log.debug("NODE ProducerService: Response entity is empty, preparing answer message");

            var requestId = response.getUpdate().getMessage().getText();
            var text = "Машина с id = " + requestId + " не найдена.";

            var sendMessage = prepareSendMessage(text, response.getUpdate());

            log.debug("NODE ProducerService: Sending answer message with car not found text");
            rabbitTemplate.convertAndSend(ANSWER_MESSAGE, sendMessage);

            return false;
        }
        return true;
    }

        public SendMessage prepareSendMessage(String message, Update update){
            var sendMessage = new SendMessage();
            sendMessage.setChatId(update.getMessage().getChatId());
            sendMessage.setText(message);
            return sendMessage;
        }
    }
