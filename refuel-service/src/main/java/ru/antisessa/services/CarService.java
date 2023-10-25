package ru.antisessa.services;

import ru.antisessa.models.Car;

import java.util.List;

public interface CarService {

    List<Car> findAll();

    Car findOne(int id);

    Car findByName(String name);

    Car findByNameIgnoreCase(String name);

    void save(Car car);

    void updateCar(Car updatedCar);

    void delete(String name);
}
