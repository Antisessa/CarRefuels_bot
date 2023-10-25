package ru.antisessa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.antisessa.models.Car;
import ru.antisessa.models.Refuel;

import java.util.List;
import java.util.Optional;

public interface RefuelRepository extends JpaRepository<Refuel, Integer> {
    Optional<Refuel> findById(int id);
    Optional<List<Refuel>> findByCar(Car car);
    Optional<Refuel> findTopByCar(Car car);

    void deleteById(Integer integer);
}
