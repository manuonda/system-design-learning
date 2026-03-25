package com.hexagonal.demo.infraestructure.in.web;

public record PostResponse(
        String id,
        String title,
        String content
) {
}
