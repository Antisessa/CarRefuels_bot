package ru.antisessa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import ru.antisessa.configuration.RabbitConfiguration;

@SpringBootApplication
@Import(RabbitConfiguration.class)
public class NodeApplication {
    public static void main(String[] args) {
        SpringApplication.run(NodeApplication.class);
    }
}
