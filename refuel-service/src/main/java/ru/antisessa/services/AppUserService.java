package ru.antisessa.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.antisessa.models.AppUser;
import ru.antisessa.repositories.AppUserRepository;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AppUserService {
    private final AppUserRepository appUserRepository;

    public Optional<AppUser> findByUpdate(Update update){
        var id = update.getMessage().getFrom().getId();

        return appUserRepository.findAppUserByTelegramUserId(id);
    }

    public Optional<AppUser> findById(long id){
        return appUserRepository.findById(id);
    }

    public Optional<AppUser> findByTelegramId(long id){
        return appUserRepository.findAppUserByTelegramUserId(id);
    }
}
