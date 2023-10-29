package ru.antisessa.services.producers;

import org.telegram.telegrambots.meta.api.objects.Update;
import ru.antisessa.models.Car;

public interface ProducerService {
    void produceAnswerFindOne(Update update, String answer);
}
