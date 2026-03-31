package com.spring4.version;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserVersionController {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserVersionController(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    // Strategy: usePathSegment — version goes in the URL path: /api/v1/users
    @GetMapping(value = "/{version}/users", version = "1.0")
    public List<UserDTOv1> getUsers(){
        return this.userRepository
                .findAll()
                .stream()
                .map(userMapper::toV1)
                .toList();
    }

    @GetMapping(value = "/{version}/users", version = "1.1")
    public List<UserDTOv1> getUsers1_1(){
        return this.userRepository
                .findAll()
                .stream()
                .map(userMapper::toV1)
                .toList();
    }

    @GetMapping(value = "/{version}/users", version = "2.0")
    public List<UserDTOv2> getUsersV2(){
        return this.userRepository
                .findAll()
                .stream()
                .map(userMapper::toV2)
                .toList();
    }

}
