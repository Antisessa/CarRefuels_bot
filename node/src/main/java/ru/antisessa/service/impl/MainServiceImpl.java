package ru.antisessa.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.antisessa.models.AppUser;
import ru.antisessa.repositories.AppUserRepository;
import ru.antisessa.service.AppUserService;
import ru.antisessa.service.MainService;
import ru.antisessa.service.ProducerService;
import ru.antisessa.service.enums.ServiceCommand;

import static ru.antisessa.enums.UserState.BASIC_STATE;
import static ru.antisessa.enums.UserState.FINDING;
import static ru.antisessa.service.enums.ServiceCommand.*;

@Log4j
@RequiredArgsConstructor
@Service
public class MainServiceImpl implements MainService {
    private final ProducerService producerService;
    private final AppUserRepository appUserRepository;
    private final AppUserService appUserService;

    @Override
    public void processTextMessage(Update update) {

        var telegramUser = update.getMessage().getFrom();
        var appUser = findOrSaveAppUser(telegramUser); // поиск и (возможно) добавление пользователя в БД
        var userState = appUser.getState(); // Получаем состояние пользователя
        var text = update.getMessage().getText(); // Получаем текстовое сообщение из update message
        var output = ""; // Переменная содержащая ответ для пользователя

        // Определяем команду из сообщения пользователя
        // cancel, registration, start
        var serviceCommand = ServiceCommand.fromValue(text);

        if (CANCEL.equals(serviceCommand)) {
            output = cancelProcess(appUser);
        } else if (BASIC_STATE.equals(userState)) {
            // если статус user - BASIC, то обрабатываем введенную им команду
            output = processServiceCommand(appUser, serviceCommand);
        } else if (FINDING.equals(userState)) {
            // если статус user - BASIC, то идем в метод поиска
            output = appUserService.findOneCar(update);
        }

        // Получаем ID чата из входящего update
        var chatId = update.getMessage().getChatId();

        sendAnswer(output, chatId);
    }


    // Обработка серверных команд
    private String processServiceCommand(AppUser appUser, ServiceCommand cmd) {
//        var serviceCommand = ServiceCommand.fromValue(cmd);

        switch (cmd) {
            case REGISTRATION:
                return "Команда в стадии разработки, попробуйте позже..."; // TODO доделать регистрацию пользователя

            case HELP:
                return help();

            case START:
                return "Приветствую" + appUser.getFirstName() + "!\n" +
                        "Чтобы посмотреть список доступных команд введите /help";
            case FIND_ONE:
                return findProcess(appUser);

            default:
                return "Неизвестная команда! Чтобы посмотреть список доступных команд введите /help";
        }
    }

    private String findProcess(AppUser appUser) {
        appUser.setState(FINDING);
        appUserRepository.save(appUser);

        return "Вы перешли в режим поиска, введите пожалуйста\n" +
                "ID машины которую хотите найти: ";
    }

    private String help() {
        return "Список доступных команд:\n"
                + "/cancel - отмена выполнения текущей команды;\n"
                + "/registration - регистрация пользователя."
                + "/find_one - поиск машины по ID";
    }

    private String cancelProcess(AppUser appUser) {
        // Назначаем пользователю базовое состояние и сохраняем изменения в БД
        appUser.setState(BASIC_STATE);
        appUserRepository.save(appUser);
        return "Команда отменена!";
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
        producerService.producerAnswer(sendMessage);
    }
}
