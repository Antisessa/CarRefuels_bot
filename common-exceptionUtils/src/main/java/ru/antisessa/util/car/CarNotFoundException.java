package ru.antisessa.util.car;

import org.telegram.telegrambots.meta.api.objects.Update;
import ru.antisessa.util.CustomCarRefuelException;

public class CarNotFoundException extends CustomCarRefuelException {
    public CarNotFoundException(String message, Update update){
        super(message, update);
    }
    public CarNotFoundException(Update update){
        super("Машина не найдена", update);
    }

    public CarNotFoundException(String message) {
        super(message);
    }

    public CarNotFoundException(){
        super("Машина не найдена");
    }
}