package com.example.hexagonal.application.service;

import com.example.hexagonal.application.port.in.CreateUserUseCase;
import com.example.hexagonal.application.port.in.GetUserUseCase;
import com.example.hexagonal.application.port.out.UserRepositoryPort;
import com.example.hexagonal.domain.model.User;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class UserService implements CreateUserUseCase, GetUserUseCase {


    private final UserRepositoryPort userRepositoryPort;
    public UserService(UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    public User createUser(User user) {
        return this.userRepositoryPort.createUser(user);
    }

    @Override
    public Optional<User> getUser(Long userId) {
        return this.userRepositoryPort.findById(userId);
    }
}
