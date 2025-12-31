package dev.stephyu.mfa.ports.out;

import dev.stephyu.mfa.domain.User;

import java.util.Optional;

public interface UserRepositoryPort {
    Optional<User> findById(String id);
    void save(User user);
}
