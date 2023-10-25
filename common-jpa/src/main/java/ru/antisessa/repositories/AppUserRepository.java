package ru.antisessa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.antisessa.models.AppUser;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findAppUserByTelegramUserId(Long id); // Поиск по telegram id
    Optional<AppUser> findById(Long id); // Поиск по id внутри БД
}