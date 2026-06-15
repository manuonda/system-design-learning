package com.hexagonal.kafka.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hexagonal.kafka.entities.Product;
import com.hexagonal.kafka.models.dto.ProductDto;
import com.hexagonal.kafka.repository.ProductRepository;



@Service
public class ProductServiceImpl implements ProductService{

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public ProductDto create(ProductDto productDto) {
        Product product = new Product(productDto.name(), productDto.price());
        Product savedProduct = productRepository.save(product);
        return new ProductDto(savedProduct.getId(), savedProduct.getName(), savedProduct.getPrice());
    }

}
