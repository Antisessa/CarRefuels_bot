package ru.antisessa.util.car;

public class CarAlreadyCreatedException extends RuntimeException{
    public CarAlreadyCreatedException(String message){
        super(message);
    }
}
