package ru.antisessa.util;

import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.Update;

@Getter
@Setter
public class CustomCarRefuelException extends RuntimeException{
    private Update update;

    public CustomCarRefuelException(String message, Update update) {
        super(message);
        this.update = update;
    }

    public CustomCarRefuelException(String message){
        super(message);
    }
}
