package ru.antisessa.util.car;

import org.telegram.telegrambots.meta.api.objects.Update;
import ru.antisessa.util.CustomCarRefuelException;

public class CarNotCreatedException extends CustomCarRefuelException {
    public CarNotCreatedException(String message, Update update) {
        super(message, update);
    }

    public CarNotCreatedException(Update update) {
        super("CarNotCreatedException");
    }

    public CarNotCreatedException(String message) {
        super(message);
    }

    public CarNotCreatedException(){
        super("Машина не создана");
    }
}
