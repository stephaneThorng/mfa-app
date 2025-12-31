package dev.stephyu.mfa.application.usecase;

import dev.stephyu.mfa.domain.User;
import dev.stephyu.mfa.ports.out.UserRepositoryPort;

public class CreateUserUseCase {
    private final UserRepositoryPort repository;

    public CreateUserUseCase(UserRepositoryPort repository) {
        this.repository = repository;
    }

    public void create(String email) {
        repository.save(new User(email, true));
    }
}
