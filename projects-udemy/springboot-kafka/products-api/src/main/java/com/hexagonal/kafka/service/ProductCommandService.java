package com.hexagonal.kafka.service;

import com.hexagonal.kafka.models.dto.ProductDto;

// Contrato para publicar comandos de productos en Kafka
public interface ProductCommandService {
  void sendCreate(ProductDto productDto);
}
