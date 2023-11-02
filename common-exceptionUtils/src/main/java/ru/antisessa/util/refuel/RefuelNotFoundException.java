package ru.antisessa.util.refuel;

import ru.antisessa.util.CustomCarRefuelException;

public class RefuelNotFoundException extends CustomCarRefuelException {
    public RefuelNotFoundException(String message){
        super(message);
    }

    public RefuelNotFoundException(){
        super("Заправка не найдена (from lambda)");
    }
}
