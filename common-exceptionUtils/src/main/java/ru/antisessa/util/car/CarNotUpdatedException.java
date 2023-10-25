package ru.antisessa.util.car;

public class CarNotUpdatedException extends RuntimeException {
    public CarNotUpdatedException(String message){
        super("CarNotUpdatedException: " + message);
    }
}
