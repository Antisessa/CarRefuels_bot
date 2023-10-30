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
}
