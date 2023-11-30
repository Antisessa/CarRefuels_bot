package ru.antisessa.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.hibernate.Hibernate;
import org.hibernate.LazyInitializationException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.antisessa.DTO.CarDTO;
import ru.antisessa.models.AppUser;

import static ru.antisessa.RabbitQueue.*;

@RequiredArgsConstructor
@Log4j
@Service
public class ProducerService {
    private final RabbitTemplate rabbitTemplate;

    public void produceAnswerMessage(Update update, String text){
        var sendMessage = new SendMessage();
        sendMessage.setChatId(update.getMessage().getChatId());
        sendMessage.setText(text);
        log.debug("RefuelService Producer: Answer message to "
                + update.getMessage().getFrom().getUserName() + " is produced");

        rabbitTemplate.convertAndSend(ANSWER_MESSAGE, sendMessage);
    }

    public void produceExceptionAnswer(Update update, String text) {
        var sendMessage = new SendMessage();
        sendMessage.setChatId(update.getMessage().getChatId());
        sendMessage.setText(text);
        log.debug("RefuelService Producer: ANSWER_MESSAGE_WITH_EXCEPTION to "
                + update.getMessage().getFrom().getUserName() + " is produced");
        rabbitTemplate.convertAndSend(ANSWER_MESSAGE_WITH_EXCEPTION, sendMessage);
    }

    public void produceAnswerFindOneCar(Update update, CarDTO.Response.GetCar answer) {
        answer.setUpdate(update);
        log.debug("RefuelService Producer: Find one car response is produced");
        rabbitTemplate.convertAndSend(FIND_ONE_CAR_RESPONSE, answer);
    }

    public void produceAnswerFindOneCarFullInfo(Update update, CarDTO.Response.GetCarFullInfo answer) {
        answer.setUpdate(update);

        log.debug("RefuelService Producer: Find one car full info response is produced");
        rabbitTemplate.convertAndSend(FIND_ONE_CAR_FULL_INFO_RESPONSE, answer);
    }

    public void produceSwitchStateToBasicRequest(AppUser appUser){
        log.debug("RefuelService Producer: Create switch state to basic request for " + appUser.getTelegramUserId());
        rabbitTemplate.convertAndSend(SWITCH_STATE_TO_BASIC, appUser);
    }

    public void produceSwitchStateToCreatingCarNameRequest(AppUser appUser){
        log.debug("RefuelService Producer: Create switch state to create car name request for "
                + appUser.getTelegramUserId());
        rabbitTemplate.convertAndSend(SWITCH_STATE_TO_CREATING_CAR_NAME, appUser);
    }

    @Transactional
    public void produceSwitchStateToCreatingCarOdometerRequest(AppUser appUser){
        log.debug("RefuelService Producer: Prepare request 'switch state to create car odometer' for "
                + appUser.getTelegramUserId());

        try {
            log.debug("AppUser proxy is initialized? - " + Hibernate.isInitialized(appUser));
            Hibernate.initialize(appUser);
            log.debug("AppUser proxy is initialized? - " + Hibernate.isInitialized(appUser));

            log.debug("AppUser's cars proxy is initialized? - " + Hibernate.isInitialized(appUser.getCars()));
            Hibernate.initialize(appUser.getCars());
            log.debug("AppUser's cars proxy is initialized? - " + Hibernate.isInitialized(appUser.getCars()));

            log.debug("Car's refuels proxy is initialized? - " + Hibernate.isInitialized(appUser.getCars().get(0).getRefuels()));
            Hibernate.initialize(appUser.getCars().get(0).getRefuels());
            log.debug("Car's refuels proxy is initialized? - " + Hibernate.isInitialized(appUser.getCars().get(0).getRefuels()));

        } catch (LazyInitializationException e) {
            log.debug("LazyInitializationException убило сервис нахуй");
        }
        log.debug("RefuelService Producer: produce request for " + appUser.getTelegramUserId());

        rabbitTemplate.convertAndSend(SWITCH_STATE_TO_CREATING_CAR_ODOMETER, appUser.getId());
    }
}

