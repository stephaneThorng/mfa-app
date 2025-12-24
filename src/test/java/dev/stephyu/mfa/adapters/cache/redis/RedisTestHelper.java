package dev.stephyu.mfa.adapters.cache.redis;

import dev.stephyu.mfa.adapters.out.cache.redis.RedisOtpEntity;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@Profile("test")
public class RedisTestHelper {
    private final RedisTemplate<String, RedisOtpEntity> redisTemplate;

    public RedisTestHelper(RedisTemplate<String, RedisOtpEntity> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void clearAllOtp() {
        Set<String> keys = redisTemplate.keys("otp:*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }
}
