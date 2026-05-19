package com.example.hexagonal.infraestructure.controller.dto;

public record UserResponse(
        Long id,
        String firstName,
        String lastName
) {
}
