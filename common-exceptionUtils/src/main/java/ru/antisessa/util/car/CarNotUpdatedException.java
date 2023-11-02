package ru.antisessa.util.car;

import org.telegram.telegrambots.meta.api.objects.Update;
import ru.antisessa.util.CustomCarRefuelException;

public class CarNotUpdatedException extends CustomCarRefuelException {
    public CarNotUpdatedException(String message, Update update){
        super(message);
    }

    public CarNotUpdatedException(Update update) {
        super("CarNotUpdatedException");
    }

    public CarNotUpdatedException(String message) {
        super(message);
    }
}
