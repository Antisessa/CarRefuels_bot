package ru.antisessa.util.refuel;

import ru.antisessa.util.CustomCarRefuelException;

public class RefuelNotDeletedException extends CustomCarRefuelException {
    public RefuelNotDeletedException(String message){
        super(message);
    }

    public RefuelNotDeletedException() {
        super("RefuelNotDeletedException");
    }
}
