package ru.antisessa.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.antisessa.utils.MessageUtils;

@Configuration
public class ServiceConfiguration {

    @Bean
    public MessageUtils messageUtils() {return new MessageUtils();}
}
