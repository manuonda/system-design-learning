package com.hexagonal.kafka.service;

import com.hexagonal.kafka.models.dto.ProductDto;

public interface ProductService {
  ProductDto create(ProductDto productDto);
}
