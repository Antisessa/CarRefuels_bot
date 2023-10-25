package ru.antisessa.util.car;

public class CarNotFoundException extends RuntimeException{
    public CarNotFoundException(String message){
        super(message);
    }
    public CarNotFoundException(){
        super("Машина не найдена");
    }
}
