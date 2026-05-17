package com.example.hexagonal.application.port.out;

import com.example.hexagonal.domain.model.User;

import java.util.Optional;

public interface UserRepositoryPort {
    User createUser(User user);
    Optional<User> findById(Long id);
}
