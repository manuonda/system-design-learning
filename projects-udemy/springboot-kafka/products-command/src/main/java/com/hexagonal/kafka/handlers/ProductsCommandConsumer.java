package com.hexagonal.kafka.handlers;


import com.hexagonal.kafka.models.Command;
import com.hexagonal.kafka.models.dto.ProductDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;


/**
 * Class configurado de kafka que esta
 */
@Configuration
public class ProductsCommandConsumer {

    private static final Logger log = LoggerFactory.getLogger(ProductsCommandConsumer.class);

    @Bean
    public Consumer<Command<ProductDto>>  handleCommands(){
     return command ->{
          log.info(String.format("Received command con type={}, id={}, body={}", command.type(), command.id(), command.body()));
           // Aquí se implementaría la lógica para manejar el comando, por ejemplo, llamar a un
     };
    }

}
