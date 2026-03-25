package com.hexagonal.demo.infraestructure.in.web;

public record CreatePostRequest(
        String title,
        String content
) {
}
