package ru.antisessa.configuration;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static ru.antisessa.RabbitQueue.*;

@Configuration
public class RabbitConfiguration {

    // Настройка JsonConverter который преобразовывает update в JSON и передает их в RabbitMQ
    // И наоборот из RabbitMQ будет парсить JSON в POJO
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Queue textMessageQueue() {
        return new Queue(TEXT_MESSAGE_UPDATE);
    }

    @Bean
    public Queue answerMessageQueue() {
        return new Queue(ANSWER_MESSAGE);
    }

    @Bean
    public Queue answerMessageWithExceptionQueue() {
        return new Queue(ANSWER_MESSAGE_WITH_EXCEPTION);
    }

    @Bean
    public Queue findOneCarRequestQueue() {
        return new Queue(FIND_ONE_CAR_REQUEST);
    }

    @Bean
    public Queue findOneCarResponseQueue() {
        return new Queue(FIND_ONE_CAR_RESPONSE);
    }

    @Bean
    public Queue findOneCarFullInfoRequestQueue() {
        return new Queue(FIND_ONE_CAR_FULL_INFO_REQUEST);
    }

    @Bean
    public Queue findOneCarFullInfoResponseQueue() {
        return new Queue(FIND_ONE_CAR_FULL_INFO_RESPONSE);
    }

    @Bean
    public Queue createCarNameRequest() {return new Queue(CREATE_CAR_NAME_REQUEST);}

    @Bean
    public Queue createCarOdometerRequest() {return new Queue(CREATE_CAR_ODOMETER_REQUEST);}

    @Bean
    public Queue createCarGasTankVolumeRequest() {return new Queue(CREATE_CAR_GAS_TANK_VOLUME_REQUEST);}

    @Bean
    public Queue createCarLastConsumptionRequest() {return new Queue(CREATE_CAR_LAST_CONSUMPTION_REQUEST);}

    @Bean
    public Queue createCarFinalValidationRequest() {return new Queue(CREATE_CAR_FINAL_VALIDATION_REQUEST);}


    // Очереди для запросов на смену User state
    @Bean
    public Queue switchStateToBasicRequest() {return new Queue(SWITCH_STATE_TO_BASIC);}

    // User states для поиска машины
    @Bean
    public Queue switchStateToFindOneCarRequest() {return new Queue(SWITCH_STATE_TO_FIND_ONE_CAR);}

    @Bean
    public Queue switchStateToFindOneCarFullInfoRequest() {return new Queue(SWITCH_STATE_TO_FIND_ONE_CAR_FULL_INFO);}

    // User states для создания машины
    @Bean
    public Queue switchStateToCreatingCarNameRequest() {return new Queue(SWITCH_STATE_TO_CREATING_CAR_NAME);}

    @Bean
    public Queue switchStateToCreatingCarOdometerRequest() {return new Queue(SWITCH_STATE_TO_CREATING_CAR_ODOMETER);}

    @Bean
    public Queue switchStateToCreatingCarGasTankVolumeRequest() {return new Queue(SWITCH_STATE_TO_CREATING_CAR_GAS_TANK_VOLUME);}

    @Bean
    public Queue switchStateToCreatingCarLastConsumptionRequest() {return new Queue(SWITCH_STATE_TO_CREATING_CAR_LAST_CONSUMPTION);}

    @Bean
    public Queue switchStateToCreatingCarFinalValidation() {return new Queue(SWITCH_STATE_TO_CREATING_CAR_FINAL_VALIDATION);}
}
