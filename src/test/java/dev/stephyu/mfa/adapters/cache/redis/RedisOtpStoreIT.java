package dev.stephyu.mfa.adapters.cache.redis;

import com.redis.testcontainers.RedisContainer;
import dev.stephyu.mfa.domain.Otp;
import dev.stephyu.mfa.ports.out.OtpStorePort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles({"test", "redis"})
@Testcontainers
class RedisOtpStoreIT {

    @Container
    static RedisContainer REDIS_CONTAINER =
            new RedisContainer(DockerImageName.parse("redis:7.2-alpine"))
                    .withExposedPorts(6379);

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);
        registry.add("spring.data.redis.port", () -> REDIS_CONTAINER.getMappedPort(6379));
    }

    @Autowired
    OtpStorePort otpStorePort;

    @Test
    void should_store_and_read_otp() {
        Otp otp = new Otp(
                "user1",
                "123456",
                Instant.now().plusSeconds(300),
                3
        );

        otpStorePort.save(otp, 300);

        Optional<Otp> loaded = otpStorePort.findByUserId("user1");

        assertThat(loaded).isPresent();
        assertThat(loaded.get().code()).isEqualTo("123456");
    }

    @Test
    void should_expire_otp() throws InterruptedException {
        Otp otp = new Otp(
                "user2",
                "654321",
                Instant.now().plusSeconds(2),
                3
        );

        otpStorePort.save(otp, 2);

        Thread.sleep(3000);

        assertThat(otpStorePort.findByUserId("user2")).isEmpty();
    }
}
