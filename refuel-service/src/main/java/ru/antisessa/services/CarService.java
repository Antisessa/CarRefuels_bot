package ru.antisessa.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.antisessa.DTO.CarDTO;
import ru.antisessa.models.Car;
import ru.antisessa.repositories.CarRepository;
import ru.antisessa.util.car.CarAlreadyCreatedException;
import ru.antisessa.util.car.CarNotFoundException;
import ru.antisessa.utils.ConverterDTO;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CarService {
    private final CarRepository carRepository;
    private final ConverterDTO converterDTO;

    ////////////// Методы для поиска //////////////
    public List<Car> findAll(){
        return carRepository.findAll();
    }

    public Car findOne(int id){
        Optional<Car> foundCar = carRepository.findById(id);
        return foundCar.orElseThrow(CarNotFoundException::new);
    }

    @Transactional
    public CarDTO.Response.GetCar findOneCar(int id){
        Optional<Car> optionalCar = carRepository.findById(id);

        if (optionalCar.isPresent()) {
            return converterDTO.carToDTO(optionalCar.get());
        } else {
            return new CarDTO.Response.GetCar();
        }

    }

    @Transactional
    public CarDTO.Response.GetCarFullInfo findOneCarFullInfo(int id) {
        Optional<Car> optionalCar = carRepository.findById(id);

        if (optionalCar.isPresent()) {
            return converterDTO.carToDTOFullInfo(optionalCar.get());
        } else {
            return new CarDTO.Response.GetCarFullInfo();
        }
    }

    public Car findByName(String name){
        Optional<Car> foundCar = carRepository.findByName(name);
        return foundCar.orElseThrow(CarNotFoundException::new);
    }

    public Car findByNameIgnoreCase(String name){
        Optional<Car> optionalCar = carRepository.findByNameIgnoreCase(name);
        return optionalCar.orElseThrow(CarNotFoundException::new);
    }

    ////////////// Методы для сохранения //////////////
    @Transactional
    public void save(Car car){
        Optional<Car> checkCreatedCar = carRepository.findByNameIgnoreCase(car.getName());
        if(checkCreatedCar.isPresent())
            throw new CarAlreadyCreatedException("Машина с таким идентификатором уже существует.");

        //Проверяем значение расхода, если оно не указано пользователем то назначаем дефолтное чтобы пропустила БД
        if(car.getLastConsumption() <= 0)
            car.setLastConsumption(1.01);

        carRepository.save(car);
    }

    @Transactional
    public void updateCar(Car updatedCar){
        // Ищем машину по переданному id
        Optional<Car> optionalCar = carRepository.findById(updatedCar.getId());
        Car previousCar = optionalCar.orElseThrow(CarNotFoundException::new);

        // Назначаем обновленной машине значения полей старой машины
        updatedCar.setLastConsumption(previousCar.getLastConsumption());
        updatedCar.setOdometer(previousCar.getOdometer());
        updatedCar.setGasTankVolume(previousCar.getGasTankVolume());
        updatedCar.setRefuels(previousCar.getRefuels());

        carRepository.save(updatedCar);
    }

    ////////////// Метод для удаления //////////////
    @Transactional
    public void delete(String name){
        Optional<Car> optionalCar = carRepository.findByNameIgnoreCase(name);
        if(optionalCar.isEmpty())
            throw new CarNotFoundException("Машины с таким идентификатором не существует.");
        carRepository.delete(optionalCar.get());

    }
}
