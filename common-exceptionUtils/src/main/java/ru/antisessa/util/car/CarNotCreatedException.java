package ru.antisessa.util.car;

public class CarNotCreatedException extends RuntimeException{
    public CarNotCreatedException(String message) {
        super(message);
    }
}
