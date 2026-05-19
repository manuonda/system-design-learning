package com.example.hexagonal.application.port.in;

import com.example.hexagonal.domain.model.User;


/**
 * Puerto de entrada
 */
public interface CreateUserUseCase {
  User createUser(User user);
}
