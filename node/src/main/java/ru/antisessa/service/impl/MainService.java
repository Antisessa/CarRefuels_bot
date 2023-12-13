package ru.antisessa.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.antisessa.models.AppUser;
import ru.antisessa.repositories.AppUserRepository;
import ru.antisessa.service.enums.ServiceCommand;

import static ru.antisessa.enums.UserState.*;
import static ru.antisessa.service.enums.ServiceCommand.*;

@Log4j
@RequiredArgsConstructor
@Service
public class MainService {
    private final ProducerService producerService;
    private final AppUserRepository appUserRepository;
    private final AppUserService appUserService;

    public void processTextMessage(Update update) {

        // Ищем пользователя в БД, если его нет то добавляем
        var appUser = findOrSaveAppUser(update.getMessage().getFrom());

        // Получаем состояние пользователя
        var userState = appUser.getState();

        // Достаем текст сообщения для обработки и chatID для ответного сообщения
        var text = update.getMessage().getText();
        var chatId = update.getMessage().getChatId();

        // Переменная содержащая ответ для пользователя
        var output = "";

        // Определяем команду, если она есть, из сообщения пользователя
        var serviceCommand = ServiceCommand.fromValue(text);

        // Если команда cancel то меняем UserState на BASIC
        if (CANCEL.equals(serviceCommand)) {
            output = cancelProcess(appUser);
            sendAnswer(output, chatId);
            return;
        }


        switch (userState) {
            case BASIC_STATE:
                output = processServiceCommand(appUser, serviceCommand);
                sendAnswer(output, chatId);
                return;

            case FINDING_ONE_CAR:
                output = appUserService.findOneCar(update);
                appUserService.switchStateToBasic(appUser.getId());
                sendAnswer(output, chatId);
                return;

            case FINDING_ONE_CAR_FULL_INFO:
                output = appUserService.findOneCarFullInfo(update);
                appUserService.switchStateToBasic(appUser.getId());
                sendAnswer(output, chatId);
                return;

            case CREATING_CAR_NAME:
                producerService.produceCreatingCarNameRequest(update);
                return;

            case CREATING_CAR_ODOMETER:
                producerService.produceCreatingCarOdometerRequest(update);
                return;

            case CREATING_CAR_GAS_TANK_VOLUME:
                producerService.produceCreatingCarGasTankVolumeRequest(update);
                return;

            case CREATING_CAR_LAST_CONSUMPTION:
                producerService.produceCreatingCarLastConsumptionRequest(update);
                return;
            case CREATING_CAR_FINAL_VALIDATION:
                producerService.produceCreatingCarFinalValidationRequest(update);
                return;
            default:
                throw new IllegalStateException("Unexpected value: " + userState);
        }
    }


    // Обработка серверных команд
    private String processServiceCommand(AppUser appUser, ServiceCommand cmd) {
        if(cmd == null) {
            return "Неизвестная команда! Чтобы посмотреть список доступных команд введите /help";
        }
        switch (cmd) {
            case REGISTRATION:
                // TODO доделать регистрацию пользователя
                return "Команда в стадии разработки, попробуйте позже...";

            case HELP:
                return help();

            case START:
                return "Приветствую" + appUser.getFirstName() + "!\n" +
                        "Чтобы посмотреть список доступных команд введите /help";
            case FIND_ONE_CAR:
                return findOneCarProcess(appUser);

            case FIND_ONE_CAR_FULL_INFO:
                return findOneCarFullInfoProcess(appUser);

            case CREATE_CAR:
                return creatingCarProcess(appUser);
            default:
                return "Неизвестная команда! Чтобы посмотреть список доступных команд введите /help";
        }
    }

    /// Методы меняющие состояние пользователя
    private String findOneCarProcess(AppUser appUser) {
        appUserService.switchStateToFindOneCar(appUser);
        return "Вы перешли в режим поиска, введите пожалуйста ID машины которую хотите найти: ";
    }

    private String findOneCarFullInfoProcess(AppUser appUser) {
        appUserService.switchStateToFindOneCarFullInfo(appUser);
        return "Вы перешли в режим поиска, введите пожалуйста ID машины которую хотите найти: ";
    }

    private String cancelProcess(AppUser appUser) {
        appUserService.switchStateToBasic(appUser.getId());
        return "Команда отменена!";
    }

    private String creatingCarProcess(AppUser appUser){
        appUserService.switchStateToCreatingCarName(appUser);
        return "Вы перешли в режим создания машины, введите пожалуйста идентификатор:";
    }

    private String help() {
        return "Список доступных команд:\n"
                + "/cancel - отмена выполнения текущей команды,\n"
                + "/registration - регистрация пользователя,\n"
                + "/find_one_car - поиск машины по ID,\n"
                + "/find_one_car_full_info - поиск машины по ID со всей информацией";
    }



    private AppUser findOrSaveAppUser(User telegramUser) {

        //Выполняем проверку наличия этого user в БД
        var optionalAppUser = appUserRepository.findAppUserByTelegramUserId(telegramUser.getId());

        if(optionalAppUser.isEmpty()) {
            var transientAppUser = AppUser.builder()
                    .telegramUserId(telegramUser.getId())
                    .username(telegramUser.getUserName())
                    .firstName(telegramUser.getFirstName())
                    .lastName(telegramUser.getLastName())
                    .isActive(true) // TODO поменять на false если понадобится
                    .state(BASIC_STATE)
                    .build();
            // Сохраняем user в БД и возвращаем его из метода, но уже с заполненным ключом id + hibernate-transient
            return appUserRepository.save(transientAppUser);
        }

        // Если user уже есть в БД, то просто возвращаем его + hibernate-transient
        return optionalAppUser.get();
    }

    private void sendAnswer(String output, Long chatId) {
        var sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(output);

        // Отправляем ответ в RabbitMQ очередь
        producerService.produceAnswer(sendMessage);
    }
}
