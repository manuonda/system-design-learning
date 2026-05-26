package com.hexagonal.kafka.controller;


import com.hexagonal.kafka.models.dto.ProductDto;
import com.hexagonal.kafka.service.ProductCommandService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductCommandService productCommandService;
    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

    public ProductController(ProductCommandService productCommandService) {
        this.productCommandService = productCommandService;
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid  @RequestBody ProductDto dto) {
       log.info("Create product {}", dto);
       this.productCommandService.sendCreate(dto);
       return ResponseEntity.ok().build();
    }

 }
