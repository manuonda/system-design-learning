package com.hexagonal.kafka.handlers;


import com.hexagonal.kafka.models.Command;
import com.hexagonal.kafka.models.dto.ProductDto;
import com.hexagonal.kafka.service.ProductService;

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

    private final ProductService productService;
    public ProductsCommandConsumer(ProductService productService) {
        this.productService = productService;
    }

    // Bean que define un consumidor de comandos para productos.
    // Este consumidor recibe comandos de tipo Command<ProductDto>
    @Bean
    public Consumer<Command<ProductDto>>  handleCommands(){
     return command ->{
          log.info("Received command con type={}, id={}, body={}", command.type(), command.id(), command.body());
          String type =  command.type();
          switch(type) {
            case "CREATE" -> {
                if (command.body() == null) {
                    log.warn("Create command body is null");
                    return;
                }
                log.info("Creating product with name={}, price={}", command.body().name(), command.body().price());
                ProductDto productDto = command.body();
                this.productService.create(productDto);
                log.info("Product created with id={}", productDto.id());
                return;
            }
            case "UPDATE" -> {}
            case "DELETE" -> {}
            case "GET" -> {}
            default -> {}

          }

        };
    }

}
