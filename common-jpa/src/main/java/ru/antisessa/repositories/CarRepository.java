package ru.antisessa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.antisessa.models.Car;
import java.util.Optional;

@Repository
public interface CarRepository extends JpaRepository<Car, Integer> {
    Optional<Car> findById(int id);
    Optional<Car> findByName(String name);
    Optional<Car> findByNameIgnoreCase(String name);
}
