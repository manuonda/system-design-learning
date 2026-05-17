package com.example.hexagonal.infraestructure.controller;


import com.example.hexagonal.application.port.in.CreateUserUseCase;
import com.example.hexagonal.application.port.in.GetUserUseCase;
import com.example.hexagonal.domain.model.User;
import com.example.hexagonal.infraestructure.controller.dto.UserRequest;
import com.example.hexagonal.infraestructure.controller.dto.UserResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/users")
public class UserController {

    private final CreateUserUseCase createUserUseCase;
    private final GetUserUseCase getUserUseCase;

    public UserController(CreateUserUseCase createUserUseCase, GetUserUseCase getUserUseCase) {
        this.createUserUseCase = createUserUseCase;
        this.getUserUseCase = getUserUseCase;
    }

    @PostMapping
    public UserResponse createUser(@RequestBody UserRequest userRequest){
        final User user = new User(null, userRequest.firstName(), userRequest.lastName());
        final User userCreated = createUserUseCase.createUser(user);
        return  new UserResponse(userCreated.id(), userCreated.firstName(), userCreated.lastName());
    }

    @GetMapping("/{id}")
    public UserResponse getUser(@PathVariable Long id) {
        final User user = this.getUserUseCase.getUser(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id " + id + " not found"));
        return new UserResponse(user.id(), user.firstName(), user.lastName());
    }
}
