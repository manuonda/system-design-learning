package com.spring4.version;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Strategy: useRequestHeader — version goes in X-API-Version header.
 * Path is always the same: /header/users
 *
 * Examples:
 *   http :8080/header/users X-API-Version:1.0
 *   http :8080/header/users X-API-Version:1.1
 *   http :8080/header/users X-API-Version:2.0
 *
 * WebConfig must be configured with: .useRequestHeader("X-API-Version")
 */
@RestController
@RequestMapping("/header")
public class UserHeaderController {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserHeaderController(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @GetMapping(value = "/users", version = "1.0")
    public List<UserDTOv1> getUsers() {
        return this.userRepository
                .findAll()
                .stream()
                .map(userMapper::toV1)
                .toList();
    }

    @GetMapping(value = "/users", version = "1.1")
    public List<UserDTOv1> getUsers1_1() {
        return this.userRepository
                .findAll()
                .stream()
                .map(userMapper::toV1)
                .toList();
    }

    @GetMapping(value = "/users", version = "2.0")
    public List<UserDTOv2> getUsersV2() {
        return this.userRepository
                .findAll()
                .stream()
                .map(userMapper::toV2)
                .toList();
    }
}