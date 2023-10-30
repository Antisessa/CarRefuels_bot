package ru.antisessa.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.antisessa.service.AppUserService;
import ru.antisessa.service.ProducerService;

@RequiredArgsConstructor
@Service
public class AppUserServiceImpl implements AppUserService {
    private final ProducerService producerService;

    @Override
    public String findOneCarFullInfo(Update update) {
        producerService.produceFindOneCarFullInfoRequest(update);
            return "Request успешно ушел в очередь, жди сигнала.";
        }
    }
