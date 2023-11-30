package ru.antisessa.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.antisessa.models.AppUser;
import ru.antisessa.services.AppUserService;
import ru.antisessa.services.CarService;
import ru.antisessa.services.TemporaryCarService;
import ru.antisessa.util.ControllerAdvice;
import ru.antisessa.utils.ConverterDTO;

import java.util.Optional;

import static ru.antisessa.RabbitQueue.*;

@RequiredArgsConstructor
@Log4j
@Service
public class ConsumerService {
    private final CarService carService;
    private final ProducerService producerService;
    private final TemporaryCarService temporaryCarService;
    private final AppUserService appUserService;
    private final ConverterDTO converterDTO;
    private final ControllerAdvice controllerAdvice;

    String exceptionMessageHasDelivered = "RefuelService Consumer: Prepared and delivered answer message with exception";

    @RabbitListener(queues = FIND_ONE_CAR_REQUEST)
    public void consumeFindOneCarRequest(Update update) {
        log.debug("RefuelService Consumer: Find one car request is received");

        int requestId = parseIntAndLogIt(update);

        var foundedCar = carService.findOneCar(requestId);

        producerService.produceAnswerFindOneCar(update, foundedCar);

    }

    @RabbitListener(queues = FIND_ONE_CAR_FULL_INFO_REQUEST)
    public void consumeFindOneCarFullInfoRequest(Update update) {
        log.debug("RefuelService Consumer: Find one car full info request is received");

        int requestId = parseIntAndLogIt(update);

        var foundedCarFullInfo = carService.findOneCarFullInfo(requestId);

        producerService.produceAnswerFindOneCarFullInfo(update, foundedCarFullInfo);

        }

    @Transactional
    @RabbitListener(queues = CREATE_CAR_NAME_REQUEST)
    public void consumeCreateCarNameRequest(Update update) {
        log.debug("RefuelService Consumer: Create car name request is received");

        //Поиск AppUser из Update
        Optional<AppUser> optionalAppUser = appUserService.findByUpdate(update);

        if(optionalAppUser.isEmpty()) {
            log.debug(exceptionMessageHasDelivered + "to " + update.getMessage().getFrom().getUserName());
            producerService.produceExceptionAnswer(update, "Ошибка! Пользователь не зарегистрирован");
            return;
        }

        var carName = update.getMessage().getText();

        //Проверка уникальности идентификатора машины
        if (!temporaryCarService.checkUniqueName(carName)) {
            log.debug(exceptionMessageHasDelivered + "to " + update.getMessage().getFrom().getUserName());
            producerService.produceExceptionAnswer(update, "Введенный вами идентификатор уже используется," +
                    " придумайте другой. Обратите внимание что регистр не учитывается");
            return;
        }

        AppUser foundUser = optionalAppUser.get();

        // Делаем запись в таблицу TemporaryCar, назначаем машине владельца и идентификатор
        temporaryCarService.recordNewCarAndSetItsName(foundUser, carName);

        // отправляем запрос на смену состояния пользователя на Creating Car Odometer
        producerService.produceSwitchStateToCreatingCarOdometerRequest(foundUser);

        // отправляем ответное сообщение пользователю
        producerService.produceAnswerMessage(update, "Машина с идентификатором " + carName
                + " отправлена в ваш гараж, введите пожалуйста показания ее одометра: ");

    }

    private int parseIntAndLogIt(Update update) {
        var textUpdate = update.getMessage().getText();
        var requestId = Integer.parseInt(textUpdate);

        log.debug("RefuelService Consumer: Parsed ID of request is " + requestId);

        return requestId;
    }
    }
