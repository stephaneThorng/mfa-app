package dev.stephyu.mfa.adapters.cache.redis;

import dev.stephyu.mfa.adapters.out.cache.redis.RedisOtpStore;
import dev.stephyu.mfa.ports.out.OtpStore;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles({"redis", "test"})
class RedisContextIT {

    @Autowired
    OtpStore otpStore;

    @Test
    void contextLoads_withRedisProfile() {
        assertThat(otpStore).isInstanceOf(RedisOtpStore.class);
    }
}
