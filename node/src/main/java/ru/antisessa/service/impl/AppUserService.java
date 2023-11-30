package ru.antisessa.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.antisessa.models.AppUser;
import ru.antisessa.repositories.AppUserRepository;
import ru.antisessa.utils.MessageUtils;

import java.util.Optional;

import static ru.antisessa.RabbitQueue.*;
import static ru.antisessa.enums.UserState.*;

@RequiredArgsConstructor
@Log4j
@Service
public class AppUserService {
    private final ProducerService producerService;
    private final AppUserRepository appUserRepository;
    private final MessageUtils messageUtils;

    String serviceName = "NODE AppUserService: Success switch ";
    String requestSuccess = "Твой запрос успешно ушел в очередь, жди сигнала.";

    @Transactional
    @RabbitListener(queues = SWITCH_STATE_TO_BASIC)
    public void switchStateToBasic(AppUser appUser){
        appUser.setState(BASIC_STATE);
        appUserRepository.save(appUser);
        log.debug(serviceName + appUser.getUsername() + " state to BASIC_STATE");
    }

    @Transactional
    @RabbitListener(queues = SWITCH_STATE_TO_FIND_ONE_CAR)
    public void switchStateToFindOneCar(AppUser appUser) {
        appUser.setState(FINDING_ONE_CAR);
        appUserRepository.save(appUser);
        log.debug(serviceName + appUser.getUsername() + " state to FINDING_ONE_CAR");
    }

    @Transactional
    @RabbitListener(queues = SWITCH_STATE_TO_FIND_ONE_CAR_FULL_INFO)
    public void switchStateToFindOneCarFullInfo(AppUser appUser) {
        appUser.setState(FINDING_ONE_CAR_FULL_INFO);
        appUserRepository.save(appUser);
        log.debug(serviceName + appUser.getUsername() + " state to FINDING_ONE_CAR_FULL_INFO");
    }

    @Transactional
    @RabbitListener(queues = SWITCH_STATE_TO_CREATING_CAR_NAME)
    public void switchStateToCreatingCarName(AppUser appUser) {
        appUser.setState(CREATING_CAR_NAME);
        appUserRepository.save(appUser);
        log.debug(serviceName + appUser.getUsername() + " state to CREATING_CAR_NAME");
    }

    @Transactional
    @RabbitListener(queues = SWITCH_STATE_TO_CREATING_CAR_ODOMETER)
    public void switchStateToCreatingCarOdometer(long id) {
        Optional<AppUser> optionalAppUser = appUserRepository.findById(id);

        if (optionalAppUser.isEmpty()) {
            log.error("Cannot switch state, AppUser is empty");
            return;
        }

        var appUser = optionalAppUser.get();

        appUser.setState(CREATING_CAR_ODOMETER);
        appUserRepository.save(appUser);
        log.debug(serviceName + appUser.getUsername() + " state to CREATING_CAR_ODOMETER");
    }

    @Transactional
    @RabbitListener(queues = SWITCH_STATE_TO_CREATING_CAR_GAS_TANK_VOLUME)
    public void switchStateToCreatingCarGasTankVolume(AppUser appUser) {
        appUser.setState(CREATING_CAR_GAS_TANK_VOLUME);
        appUserRepository.save(appUser);
        log.debug(serviceName + appUser.getUsername() + " state to CREATING_CAR_VOLUME");
    }

    @Transactional
    @RabbitListener(queues = SWITCH_STATE_TO_CREATING_CAR_LAST_CONSUMPTION)
    public void switchStateToCreatingCarLastConsumption(AppUser appUser) {
        appUser.setState(CREATING_CAR_LAST_CONSUMPTION);
        appUserRepository.save(appUser);
        log.debug(serviceName + appUser.getUsername() + " state to CREATING_LAST_CONSUMPTION");
    }

    public String findOneCar(Update update) {
        producerService.produceFindOneCarRequest(update);
        return requestSuccess;
    }

    public String findOneCarFullInfo(Update update) {
        producerService.produceFindOneCarFullInfoRequest(update);
        return requestSuccess;
        }
}
