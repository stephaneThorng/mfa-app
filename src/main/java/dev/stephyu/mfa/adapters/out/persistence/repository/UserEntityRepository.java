package dev.stephyu.mfa.adapters.out.persistence.repository;

import dev.stephyu.mfa.adapters.out.persistence.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;

public interface UserEntityRepository extends CrudRepository<UserEntity, String> {
}
