package com.hexagonal.kafka.service;

import com.hexagonal.kafka.models.Command;
import com.hexagonal.kafka.models.dto.ProductDto;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

// Publica comandos de productos al topic de Kafka definido en el binding "commands-out-0"
// StreamBridge permite enviar mensajes a Kafka desde cualquier clase sin usar Supplier<T>
@Service
public class ProductCommandServiceImpl implements ProductCommandService {

    // Nombre del binding definido en application.yaml → commands-out-0.destination=products.commands
    private static final String BINDING_NAME = "commands-out-0";

    // StreamBridge es un componente de Spring Cloud Stream 
    // que permite enviar mensajes a Kafka desde cualquier clase sin usar Supplier<T>
    private final StreamBridge streamBridge;

    public ProductCommandServiceImpl(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    // Empaqueta el DTO en un Command y lo envía al topic. Lanza excepción si Kafka no lo acepta.
    @Override
    public void sendCreate(ProductDto productDto) {
        Command<ProductDto> cmd = new Command<>("CREATE", null, productDto);
        //envio al topic para almacenar el mensaje
        boolean send = this.streamBridge.send(BINDING_NAME, cmd);

        if (!send) {
            throw new IllegalStateException("No se pudo enviar el comando de kafka");
        }
    }
}
