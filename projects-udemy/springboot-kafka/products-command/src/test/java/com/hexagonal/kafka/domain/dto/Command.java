package com.hexagonal.kafka.domain.dto;

public record Command<T>(String type, Long id,  T value) {
}
