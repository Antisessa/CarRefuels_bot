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

        var telegramUser = update.getMessage().getFrom();
        var appUser = findOrSaveAppUser(telegramUser); // поиск и (возможно) добавление пользователя в БД
        var userState = appUser.getState(); // Получаем состояние пользователя
        var text = update.getMessage().getText(); // Получаем текстовое сообщение из update message
        var output = ""; // Переменная содержащая ответ для пользователя
        var chatId = update.getMessage().getChatId(); // Получаем ID чата из входящего update

        // Определяем команду из сообщения пользователя
        // cancel, registration, start
        var serviceCommand = ServiceCommand.fromValue(text);

        if (CANCEL.equals(serviceCommand)) {
            output = cancelProcess(appUser);

        } else if (BASIC_STATE.equals(userState)) {
            // если статус user - BASIC, то обрабатываем введенную им команду
            output = processServiceCommand(appUser, serviceCommand);

        } else if (FINDING_ONE_CAR.equals(userState)) {
            // если статус user - BASIC, то идем в метод поиска
            output = appUserService.findOneCar(update);
            appUserService.switchStateToBasic(appUser);

        } else if (FINDING_ONE_CAR_FULL_INFO.equals(userState)) {
            // если статус user - BASIC, то идем в метод поиска
            output = appUserService.findOneCarFullInfo(update);
            appUserService.switchStateToBasic(appUser);
        }

        // В конечном итоге после выполнения одного из блоков if мы получаем ответ для пользователя
        sendAnswer(output, chatId);
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
        appUserService.switchStateToBasic(appUser);
        return "Команда отменена!";
    }

    private String help() {
        return "Список доступных команд:\n"
                + "/cancel - отмена выполнения текущей команды,\n"
                + "/registration - регистрация пользователя,\n"
                + "/find_one_car - поиск машины по ID,\n"
                + "/find_one_car_full_info - поиск машины по ID со всей информацией";
    }



    private AppUser findOrSaveAppUser(User telegramUser) {
//        // Достаем User из Update
//        var telegramUser = update.getMessage().getFrom();

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
