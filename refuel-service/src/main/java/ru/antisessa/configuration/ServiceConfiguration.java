package ru.antisessa.configuration;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.antisessa.utils.ConverterDTO;

@Configuration
public class ServiceConfiguration {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public ConverterDTO converterDTO() {return new ConverterDTO();}
}
