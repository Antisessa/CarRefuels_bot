package ru.antisessa.configuration;

import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.antisessa.utils.ConverterDTO;

@Configuration
public class ServiceConfiguration {

    @Bean
    public ModelMapper modelMapper() {
//        ModelMapper modelMapper = new ModelMapper();
//        modelMapper.registerModule()
        return new ModelMapper();
    }

    @Bean
    public ConverterDTO converterDTO() {return new ConverterDTO();}
}
