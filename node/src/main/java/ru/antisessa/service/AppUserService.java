package ru.antisessa.service;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface AppUserService {
    String findOneCarFullInfo(Update update);
}
