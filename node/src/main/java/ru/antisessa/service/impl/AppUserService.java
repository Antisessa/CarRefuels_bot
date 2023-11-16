package ru.antisessa.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.antisessa.enums.UserState;
import ru.antisessa.models.AppUser;
import ru.antisessa.repositories.AppUserRepository;

import static ru.antisessa.enums.UserState.*;

@RequiredArgsConstructor
@Log4j
@Service
public class AppUserService {
    private final ProducerService producerService;
    private final AppUserRepository appUserRepository;

    String serviceName = "NODE AppUserService: Success switch ";
    String requestSuccess = "Твой запрос успешно ушел в очередь, жди сигнала.";

    public void switchStateToBasic(AppUser appUser){
        appUser.setState(BASIC_STATE);
        appUserRepository.save(appUser);
        log.debug(serviceName + appUser.getUsername() + " state to BASIC_STATE");
    }

    public void switchStateToFindOneCar(AppUser appUser) {
        appUser.setState(FINDING_ONE_CAR);
        appUserRepository.save(appUser);
        log.debug(serviceName + appUser.getUsername() + " state to FINDING_ONE_CAR");
    }

    public void switchStateToFindOneCarFullInfo(AppUser appUser) {
        appUser.setState(FINDING_ONE_CAR_FULL_INFO);
        appUserRepository.save(appUser);
        log.debug(serviceName + appUser.getUsername() + " state to FINDING_ONE_CAR_FULL_INFO");
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
