import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.antisessa.DTO.CarDTO;
import ru.antisessa.NodeApplication;
import ru.antisessa.models.Car;
import ru.antisessa.repositories.CarRepository;
import ru.antisessa.service.impl.ProducerService;
import ru.antisessa.utils.ConverterDTO;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = NodeApplication.class)
class ProducerServiceTest {
    private final ProducerService producerService;
    private final CarRepository carRepository;
    private final ConverterDTO converterDTO;

    @Autowired
    public ProducerServiceTest(ProducerService producerService,
                               CarRepository carRepository,
                               ConverterDTO converterDTO) {
        this.producerService = producerService;
        this.carRepository = carRepository;
        this.converterDTO = converterDTO;
    }

    @Test
    @Transactional
    void testWildcardsMethod() throws Exception {
        Car transientCar = carRepository.findById(5).orElseThrow(() -> new Exception("Car not found"));

        Update update = new Update();
        update.setMessage(new Message());

        CarDTO.Response.GetCar carDTO = converterDTO.carToDTO(transientCar);
        carDTO.setUpdate(update);

        CarDTO.Response.GetCarFullInfo carDTOFullInfo = converterDTO.carToDTOFullInfo(transientCar);
        carDTOFullInfo.setUpdate(update);

        assertTrue(producerService.validateResponse(carDTO));

        assertTrue(producerService.validateResponse(carDTOFullInfo));

    }
}
