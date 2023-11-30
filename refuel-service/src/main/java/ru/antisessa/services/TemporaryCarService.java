package ru.antisessa.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.antisessa.models.AppUser;
import ru.antisessa.models.Car;
import ru.antisessa.models.TemporaryCar;
import ru.antisessa.repositories.TemporaryCarRepository;
import ru.antisessa.util.car.CarNotFoundException;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j
@RequiredArgsConstructor
@Service
public class TemporaryCarService {
    private final TemporaryCarRepository temporaryCarRepository;
    private final CarService carService;

    public TemporaryCar findOneByAppUser(AppUser appUser){
        Optional<TemporaryCar> foundCar = temporaryCarRepository.findByAppUser(appUser);
        return foundCar.orElse(null);
    }

    @Transactional
    public void save(TemporaryCar temporaryCar, AppUser appUser){
        temporaryCar.setAppUser(appUser);
        temporaryCarRepository.save(temporaryCar);
    }

    @Transactional
    public void setName(TemporaryCar temporaryCar, String name){
        temporaryCar.setName(name);
        temporaryCarRepository.save(temporaryCar);
    }

    @Transactional
    public void recordNewCarAndSetItsName(AppUser appUser, String name){
        TemporaryCar temporaryCar = new TemporaryCar();
        temporaryCar.setAppUser(appUser);
        temporaryCar.setName(name);
        temporaryCarRepository.save(temporaryCar);
        log.debug("TemporaryCarService: create new temporary car - " + name + ", owner " + appUser);
    }

    @Transactional
    public void setOdometer(TemporaryCar temporaryCar, int odometer){
        temporaryCar.setOdometer(odometer);
        temporaryCarRepository.save(temporaryCar);
    }

    @Transactional
    public void setGasTankVolume(TemporaryCar temporaryCar, int volume){
        temporaryCar.setGasTankVolume(volume);
        temporaryCarRepository.save(temporaryCar);
    }

    @Transactional
    public void setConsumption(TemporaryCar temporaryCar, double consumption){
        temporaryCar.setLastConsumption(consumption);
        temporaryCarRepository.save(temporaryCar);
    }

    public TemporaryCar findOne(int id){
        Optional<TemporaryCar> foundCar = temporaryCarRepository.findById(id);
        return foundCar.orElseThrow(CarNotFoundException::new);
    }

    public boolean checkUniqueName(String name) {
        List<Car> existsCars = carService.findAllModel();

        List<Car> filteredCars =
                existsCars.stream().filter(a -> a.getName().equalsIgnoreCase(name))
                        .collect(Collectors.toList());

        if(!filteredCars.isEmpty()) {
            log.debug("Найдено совпадение, введенный идентификатор " + name
                    + " соответствует существующему " + filteredCars.get(0).getName());
        }

        return filteredCars.isEmpty();
    }
}
