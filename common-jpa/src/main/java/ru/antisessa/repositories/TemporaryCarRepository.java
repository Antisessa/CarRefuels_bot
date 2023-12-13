package ru.antisessa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.antisessa.models.AppUser;
import ru.antisessa.models.Car;
import ru.antisessa.models.TemporaryCar;

import java.util.List;
import java.util.Optional;

@Repository
public interface TemporaryCarRepository extends JpaRepository<TemporaryCar, Integer> {
    Optional<TemporaryCar> findById(int id);
    Optional<TemporaryCar> findByName(String name);
    Optional<TemporaryCar> findByNameIgnoreCase(String name);
    Optional<TemporaryCar> findByAppUser(AppUser appUser);
    List<TemporaryCar> findTemporaryCarsByAppUser(AppUser appUser);
    void deleteAllByAppUser(AppUser appUser);
}
