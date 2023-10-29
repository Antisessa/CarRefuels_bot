package ru.antisessa.services.consumers;


import org.telegram.telegrambots.meta.api.objects.Update;

public interface ConsumerService {
    void consumeFindOneCar(Update update);
}
