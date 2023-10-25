package ru.antisessa.services;

import ru.antisessa.models.Car;
import ru.antisessa.models.Refuel;

import java.util.List;

public interface RefuelService {

    Refuel findOne(int id);

    List<Refuel> findAll();

    List<Refuel> findByCar(Car car);

    void save(Refuel refuel);

    void updateLastRefuel(Refuel updatedRefuel);

    void deleteLastRefuel(String carName);

    double calculateAndValidate(Refuel refuel, Car car);


}
