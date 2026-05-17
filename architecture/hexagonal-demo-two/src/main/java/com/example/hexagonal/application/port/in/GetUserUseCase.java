package com.example.hexagonal.application.port.in;

import com.example.hexagonal.domain.model.User;

import java.util.Optional;

public interface GetUserUseCase {
    Optional<User> getUser(Long userId);
}
