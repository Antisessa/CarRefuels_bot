package ru.antisessa.util.refuel;

public class RefuelNotDeletedException extends RuntimeException {
    public RefuelNotDeletedException(String message){
        super(message);
    }
}
