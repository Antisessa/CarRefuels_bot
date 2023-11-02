package ru.antisessa.util.refuel;

import org.telegram.telegrambots.meta.api.objects.Update;
import ru.antisessa.util.CustomCarRefuelException;

public class RefuelValidateException extends CustomCarRefuelException {
    public RefuelValidateException(String message) {
        super(message);
    }
    public RefuelValidateException(Update update) {
        super("RefuelValidateException", update);
    }
}
