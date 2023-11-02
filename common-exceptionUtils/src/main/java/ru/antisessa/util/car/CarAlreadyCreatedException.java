package ru.antisessa.util.car;

import org.telegram.telegrambots.meta.api.objects.Update;
import ru.antisessa.util.CustomCarRefuelException;

public class CarAlreadyCreatedException extends CustomCarRefuelException {
    public CarAlreadyCreatedException(String message, Update update) {
        super(message, update);
    }

    public CarAlreadyCreatedException(Update update) {
        super("CarAlreadyCreatedException", update);
    }

    public CarAlreadyCreatedException(String message) {
        super(message);
    }

    public CarAlreadyCreatedException(){
        super("Машина уже существует");
    }
}
