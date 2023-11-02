package ru.antisessa.util.refuel;

import ru.antisessa.util.CustomCarRefuelException;

public class RefuelNotUpdatedException extends CustomCarRefuelException {
    public RefuelNotUpdatedException(String message){
        super(message);
    }

    public RefuelNotUpdatedException() {
        super("RefuelNotUpdatedException");
    }
}
