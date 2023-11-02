package ru.antisessa.util.refuel;

import ru.antisessa.util.CustomCarRefuelException;

public class RefuelNotCreatedException extends CustomCarRefuelException {
    public RefuelNotCreatedException(String message){
        super(message);
    }

    public RefuelNotCreatedException() {
        super("RefuelNotCreatedException");
    }
}
