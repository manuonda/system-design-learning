package com.hexagonal.kafka.models.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// Datos del producto recibidos en el request. name no puede ser vacio, price minimo 10.
public record ProductDto(@NotBlank String name, @NotNull @Min(10) Double price) {
}
