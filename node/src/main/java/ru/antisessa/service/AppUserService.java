package ru.antisessa.service;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface AppUserService {
    String findOneCar(Update update);
}
