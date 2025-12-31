package dev.stephyu.mfa.adapters.persistence;

import dev.stephyu.mfa.ports.out.UserRepositoryPort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest
@ActiveProfiles({"test", "inmemory"})
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
public class UserRepositoryPortAdapterIT {
    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16");

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    UserRepositoryPort userRepositoryPort;

    @Test
    void should_save_and_load_user() {
        var user = new dev.stephyu.mfa.domain.User(
                "misc@stephyu.dev",
                true
        );
        userRepositoryPort.save(user);
        var loaded = userRepositoryPort.findById("misc@stephyu.dev");
        assert(loaded.isPresent());
        assert(loaded.get().isMfaEnabled());

    }

}
