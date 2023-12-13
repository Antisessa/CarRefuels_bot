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
        log.debug("RefuelService Consumer: Request 'create car name' is received");

        Optional<AppUser> optionalAppUser = extractAppUserFromUpdate(update);

        if (optionalAppUser.isEmpty()) {
            return;
        }

        // Парсим строку с идентификатором машины из update
        var carName = update.getMessage().getText();

        // Проверяем уникальность идентификатора машины
        if (!temporaryCarService.checkUniqueName(carName)) {
            log.debug(exceptionMessageHasDelivered + "to " + update.getMessage().getFrom().getUserName());
            producerService.produceExceptionAnswer(update, "Введенный вами идентификатор уже используется, " +
                    "придумайте другой. Обратите внимание что в идентификаторе регистр не учитывается");
            return;
        }

        AppUser foundUser = optionalAppUser.get();

        // Делаем запись в таблицу TemporaryCar, назначаем машине владельца и идентификатор
        temporaryCarService.recordNewCarAndSetItsName(foundUser, carName);

        // отправляем запрос на смену состояния пользователя на Creating Car Odometer
        producerService.produceSwitchStateRequest(foundUser.getId(), SWITCH_STATE_TO_CREATING_CAR_ODOMETER);

        // отправляем ответное сообщение пользователю
        producerService.produceAnswerMessage(update, "Машина с идентификатором " + carName
                + " отправлена в ваш гараж, введите пожалуйста показания ее одометра: ");

    }

    @Transactional
    @RabbitListener(queues = CREATE_CAR_ODOMETER_REQUEST)
    public void consumeCreatingCarOdometerRequest(Update update) {
        log.debug("RefuelService Consumer: Request 'create car odometer record' is received");


        Optional<AppUser> optionalAppUser = extractAppUserFromUpdate(update);

        if (optionalAppUser.isEmpty()) {
            return;
        }

        int odometerRecord = parseIntOrSendMessageWithException(update);

        // Если спарсенное значение равно -1, то значит была выброшена ошибка
        if(odometerRecord == -1) {
            return;
        }

        AppUser foundUser = optionalAppUser.get();

        // Ищем временную машину по пользователю
        var optionalTemporaryCar = temporaryCarService.findOneByAppUser(foundUser);

        // Если машина найдена - сохраняем ей значение одометра, меняем userState и высылаем пользователю ответ
        if (optionalTemporaryCar.isPresent()) {
            var foundCar = optionalTemporaryCar.get();
            temporaryCarService.setOdometer(foundCar, odometerRecord);
            producerService.produceSwitchStateRequest(foundUser.getId(), SWITCH_STATE_TO_CREATING_CAR_GAS_TANK_VOLUME);
            producerService.produceAnswerMessage(update, "Значение одометра для создаваемой вами " +
                    "машины - " + odometerRecord + "\nВведите пожалуйста объем топливного бака: ");

        } else {
            producerService.produceExceptionAnswer(update, "Возникла ошибка при поиске создаваемой машины");
        }

    }

    @Transactional
    @RabbitListener(queues = CREATE_CAR_GAS_TANK_VOLUME_REQUEST)
    public void consumeCreatingCarGasTankVolumeRequest(Update update) {
        log.debug("RefuelService Consumer: Request 'create car gas tank volume' is received");

        Optional<AppUser> optionalAppUser = extractAppUserFromUpdate(update);

        if (optionalAppUser.isEmpty()) {
            return;
        }

        int gasTankVolume = parseIntOrSendMessageWithException(update);

        // Если спарсенное значение равно -1, то значит была выброшена ошибка
        if(gasTankVolume == -1) {
            return;
        }

        AppUser foundUser = optionalAppUser.get();

        // Ищем временную машину по пользователю
        var optionalTemporaryCar = temporaryCarService.findOneByAppUser(foundUser);

        // Если машина найдена - сохраняем ей значение объема бензобака, меняем userState и высылаем пользователю ответ
        if (optionalTemporaryCar.isPresent()) {
            var foundCar = optionalTemporaryCar.get();
            temporaryCarService.setGasTankVolume(foundCar, gasTankVolume);
            producerService.produceSwitchStateRequest(foundUser.getId(), SWITCH_STATE_TO_CREATING_CAR_LAST_CONSUMPTION);
            producerService.produceAnswerMessage(update, "Объем бензобака указанный вами -  " +
                    gasTankVolume + ",\nВведите пожалуйста последнее известное значение расхода, " +
                    "разделяя целую и дробную часть точкой: ");

        } else {
            producerService.produceExceptionAnswer(update, "Возникла ошибка при поиске машины которую вы создаете, " +
                    "пожалуйста введите /cancel и попробуйте еще раз");
        }
    }

    @Transactional
    @RabbitListener(queues = CREATE_CAR_LAST_CONSUMPTION_REQUEST)
    public void consumeCreatingCarLastConsumptionRequest(Update update){
        log.debug("RefuelService Consumer: Request 'create car last consumption' is received");

        Optional<AppUser> optionalAppUser = extractAppUserFromUpdate(update);

        if (optionalAppUser.isEmpty()) {
            return;
        }

        double consumption = parseDoubleOrSendMessageWithException(update);

        // Если значение равно -1.0, то значит в методе было выброшено исключение
        if (consumption == -1.0) {
            producerService.produceAnswerMessage(update, "Введите пожалуйста значение еще раз: ");
            return;
        }

        AppUser foundUser = optionalAppUser.get();

        // Ищем временную машину по пользователю
        var optionalTemporaryCar = temporaryCarService.findOneByAppUser(foundUser);

        // Если машина найдена - сохраняем ей значение расхода, меняем userState
        if (optionalTemporaryCar.isPresent()) {
            var foundCar = optionalTemporaryCar.get();
            temporaryCarService.setLastConsumption(foundCar, consumption);
            producerService.produceSwitchStateRequest(foundUser.getId(), SWITCH_STATE_TO_CREATING_CAR_FINAL_VALIDATION);
//            producerService.produceAnswerMessage(update, "Введенное вами значение равно " + consumption);

            StringBuilder sb = new StringBuilder();

            sb.append("Готово!");
            sb.append("\nВы закончили создание машины с идентификатором - ").append(foundCar.getName());
            sb.append("\nЗаписанное значение одометра - ").append(foundCar.getOdometer());
            sb.append("\nУказанный вами объем бензобака - ").append(foundCar.getGasTankVolume());
            sb.append("\nПоследнее значение расхода топлива - ").append(consumption);
            sb.append("\n\nПроверьте правильность данных. Если ошибок нет - напишите 'Да', " +
                    "\nПри наличии ошибок введите команду /cancel и попробуйте заново");

            producerService.produceAnswerMessage(update, sb.toString());

        } else {
            producerService.produceExceptionAnswer(update, "Возникла ошибка при поиске машины которую вы создаете, " +
                    "пожалуйста введите /cancel и попробуйте еще раз");
        }

    }

    @Transactional
    @RabbitListener(queues = CREATE_CAR_FINAL_VALIDATION_REQUEST)
    public void consumeCreatingCarFinalValidationRequest(Update update){
        log.debug("RefuelService Consumer: Request 'create car final validation' is received");

        var textMessage = update.getMessage().getText();

        if(!textMessage.equalsIgnoreCase("да")) {
            producerService.produceAnswerMessage(update, "Проверьте правильность данных. " +
                    "Если ошибок нет - напишите 'Да'," +
                    "\nПри наличии ошибок введите команду /cancel и попробуйте заново");
            return;
        }

        Optional<AppUser> optionalAppUser = extractAppUserFromUpdate(update);

        if (optionalAppUser.isEmpty()) {
            return;
        }

        AppUser foundUser = optionalAppUser.get();

        // Ищем временную машину по пользователю
        var optionalTemporaryCar = temporaryCarService.findOneByAppUser(foundUser);

        // Если машина найдена - сохраняем ей значение расхода, меняем userState
        if (optionalTemporaryCar.isPresent()) {
            log.debug("RefuelService Consumer: Prepare to migrate temporary car to origin");
            carService.migrate(optionalTemporaryCar.get());
            producerService.produceSwitchStateRequest(foundUser.getId(), SWITCH_STATE_TO_BASIC);
            temporaryCarService.truncateAllForUser(foundUser);
            producerService.produceAnswerMessage(update, "Ваша новая машина успешно сохранена");
        } else {
            producerService.produceExceptionAnswer(update, "Возникла ошибка при поиске машины которую вы создаете, " +
                    "пожалуйста введите /cancel и попробуйте еще раз");
        }


    }

    private Optional<AppUser> extractAppUserFromUpdate(Update update) {

        //Поиск Optional AppUser из Update
        var optionalAppUser = appUserService.findByUpdate(update);

        // Если пользователь не найден в БД, то ему будет отправлено сообщение с ошибкой
        if (optionalAppUser.isEmpty()) {
            log.debug(exceptionMessageHasDelivered + "to " + update.getMessage().getFrom().getUserName());
            producerService.produceExceptionAnswer(update, "Ошибка! Пользователь не зарегистрирован");
        }

        return optionalAppUser;
    }

    private int parseIntAndLogIt(Update update) {
        var textUpdate = update.getMessage().getText();
        var requestId = Integer.parseInt(textUpdate);

        log.debug("RefuelService Consumer: Parsed ID of request is " + requestId);

        return requestId;
    }

    private double parseDoubleOrSendMessageWithException(Update update){
        var textMessage = update.getMessage().getText();
        double parsedDouble = -1.0;

        try {
            parsedDouble = Double.parseDouble(textMessage);
        } catch (NumberFormatException e) {
            log.error("RefuelService Consumer: parsing double and catch exception " + e.getClass());
            producerService.produceExceptionAnswer(update, "Возникла ошибка при получении значения расхода, " +
                    "проверьте пожалуйста что написали положительное числовое значение " +
                    "формата 7.8 (разделитель - точка)");
        } catch (NullPointerException e) {
            log.error("RefuelService Consumer: parsing double and catch exception " + e.getClass());
            producerService.produceExceptionAnswer(update, "Возникла ошибка при получении значения расхода, " +
                    "возможно вы ввели пустое значение");
        }

        if (parsedDouble <= 0.0) {
            log.error("RefuelService Consumer: parsed double is negative or zero");
            producerService.produceExceptionAnswer(update, "Возникла ошибка при получении значения расхода, " +
                    "возможно вы ввели отрицательное значение или ноль");
            parsedDouble = -1.0;
        }

        return parsedDouble;
    }

    private int parseIntOrSendMessageWithException(Update update) {
        // Парсим текст сообщения из update
        var textMessage = update.getMessage().getText();
        int parsedInteger = -1;

        try {
            parsedInteger = Integer.parseInt(textMessage);

        } catch (NumberFormatException e) {
            log.error("RefuelService Consumer: parsing integer and catch exception " + e.getClass());
            producerService.produceExceptionAnswer(update, "Возникла ошибка при получении значения одометра, " +
                    "проверьте пожалуйста что написали числовое значение без прочих символов");
        }

        return parsedInteger;
    }
}
