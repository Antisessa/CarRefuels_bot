package ru.antisessa.utils;

import org.modelmapper.ModelMapper;
import org.springframework.transaction.annotation.Transactional;
import ru.antisessa.DTO.CarDTO;
import ru.antisessa.DTO.RefuelDTO;
import ru.antisessa.models.Car;
import ru.antisessa.models.Refuel;

import java.util.stream.Collectors;

public class ConverterDTO {
    ModelMapper modelMapper = new ModelMapper();

    /*TODO перенести сюда все методы carToDTO, refuelToDTO
       дописать такой же класс но DTOtoModel, + возвращать не model, а DTO
     */

    /////// Code Block for convert Model to DTO ///////
    // Convert Car to GetCar
    public CarDTO.Response.GetCar carToDTO(Car car) {
        CarDTO.Response.GetCar getCar = modelMapper.map(car, CarDTO.Response.GetCar.class);

        getCar.setCountRefuels(car.getRefuels().size());

        return getCar;
    }

    @Transactional
    // Convert Car to GetCarFullInfo
    public CarDTO.Response.GetCarFullInfo carToDTOFullInfo(Car car) {
        CarDTO.Response.GetCarFullInfo getCar = modelMapper.map(car, CarDTO.Response.GetCarFullInfo.class);

        getCar.setCountRefuels(car.getRefuels().size());

        getCar.setRefuels(car.getRefuels().stream()
                .map(this::refuelToDTOFullInfo).collect(Collectors.toList()));

        return getCar;
    }

    // Convert Refuel to GetRefuel
    public RefuelDTO.Response.GetRefuel refuelToDTO(Refuel refuel) {
        return modelMapper.map(refuel, RefuelDTO.Response.GetRefuel.class);
    }

    // Convert Refuel to GetRefuelFullInfo
    public RefuelDTO.Response.GetRefuelFullInfo refuelToDTOFullInfo(Refuel refuel) {
        return modelMapper.map(refuel, RefuelDTO.Response.GetRefuelFullInfo.class);
    }

    /////// Code Block for convert DTO to Model ///////
    //  Convert CreateCar to Car
    public Car convertToCar(CarDTO.Request.CreateCar createCar){
        return modelMapper.map(createCar, Car.class);
    }

    // Convert UpdateCar to Car
    public Car convertToCar(CarDTO.Request.UpdateCar updateCar){
        return modelMapper.map(updateCar, Car.class);
    }

}
