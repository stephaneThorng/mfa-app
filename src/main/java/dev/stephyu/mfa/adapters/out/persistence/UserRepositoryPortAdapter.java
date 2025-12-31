package dev.stephyu.mfa.adapters.out.persistence;

import dev.stephyu.mfa.adapters.out.persistence.repository.UserEntityRepository;
import dev.stephyu.mfa.domain.User;
import dev.stephyu.mfa.ports.out.UserRepositoryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserRepositoryPortAdapter implements UserRepositoryPort {

    @Autowired
    UserEntityRepository repository;

    @Override
    public Optional<User> findById(String email) {
        return repository.findById(email).stream().findFirst().map(
                entity -> new User(
                        entity.getEmail(),
                        entity.isMfaEnabled()
                )
        );
    }

    @Override
    public void save(User user) {
        var entity = new dev.stephyu.mfa.adapters.out.persistence.entity.UserEntity();
        entity.setEmail(user.getEmail());
        entity.setMfaEnabled(user.isMfaEnabled());
        repository.save(entity);
    }
}
