package dev.stephyu.mfa.adapters.cache.redis;

import dev.stephyu.mfa.adapters.out.cache.redis.RedisOtpStoreAdapter;
import dev.stephyu.mfa.ports.out.OtpStorePort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles({"test", "redis"})
class RedisContextIT {

    @Autowired
    OtpStorePort otpStorePort;

    @Test
    void contextLoads_withRedisProfile() {
        assertThat(otpStorePort).isInstanceOf(RedisOtpStoreAdapter.class);
    }
}
