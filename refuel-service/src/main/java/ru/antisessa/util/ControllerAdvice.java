package ru.antisessa.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.hibernate.LazyInitializationException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.antisessa.util.car.CarNotFoundException;

import static ru.antisessa.RabbitQueue.ANSWER_MESSAGE_WITH_EXCEPTION;

@Log4j
@RequiredArgsConstructor
@org.springframework.web.bind.annotation.ControllerAdvice
public class ControllerAdvice {
    private final RabbitTemplate rabbitTemplate;

    @ExceptionHandler()
    public void handleRuntimeException(RuntimeException e) {
        log.fatal(e.getMessage());
    }

    @ExceptionHandler()
    public void handleLazyInitializationException(LazyInitializationException e) {
        log.fatal(e.getMessage());
    }

    @ExceptionHandler()
    public void handleListenerExecutionFailedException(ListenerExecutionFailedException e) {
        log.fatal(e.getMessage());
    }

    @ExceptionHandler()
    public void handleCarNotFoundException(CarNotFoundException e) {
        log.debug("ControllerAdvice on RefuelService: " + e.getMessage());

        var sendMessage = prepareSendMessage(e.getUpdate(), e);

        rabbitTemplate.convertAndSend(ANSWER_MESSAGE_WITH_EXCEPTION, sendMessage);
    }

//    @ExceptionHandler(CustomCarRefuelException.class)
//    public ResponseEntity<String> handle(CustomCarRefuelException e) {
//        log.debug("ControllerAdvice on RefuelService: " + e.getMessage());
//
//        var sendMessage = prepareSendMessage(e.getUpdate(), e);
//
//        rabbitTemplate.convertAndSend(ANSWER_MESSAGE_WITH_EXCEPTION, sendMessage);
//
//        return ResponseEntity.ok().build();
//    }

    private SendMessage prepareSendMessage(Update update, Exception e) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getMessage().getChatId());
        sendMessage.setText(e.getMessage());

        return sendMessage;
    }
}
