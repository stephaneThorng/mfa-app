package dev.stephyu.mfa.adapters.in.rest;

import dev.stephyu.mfa.application.usecase.CreateUserUseCase;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    private final CreateUserUseCase useCase;

    public UserController(CreateUserUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping
    public void create(@RequestBody CreateUserRequest request) {
        useCase.create(request.email());
    }

    public record CreateUserRequest(String email) {}
}