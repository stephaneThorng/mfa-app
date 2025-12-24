package dev.stephyu.mfa.adapters.out.cache.redis;

import dev.stephyu.mfa.domain.Otp;
import dev.stephyu.mfa.ports.out.OtpStore;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

@Profile({"redis", "redis-standalone", "redis-cluster"})
@Component
public class RedisOtpStore implements OtpStore {
    private final RedisTemplate<String, RedisOtpEntity> redisTemplate;

    @Autowired
    private Environment environment;

    public RedisOtpStore(RedisTemplate<String, RedisOtpEntity> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    public void init() {
        System.out.println(">>> Active profiles: " + Arrays.toString(environment.getActiveProfiles()));
        System.out.println(">>> RedisOtpStore connected to " + redisTemplate.getConnectionFactory());
    }


    @Override
    public void save(Otp otp, long ttlSeconds) {
        String key = "otp:" + otp.userId();
        RedisOtpEntity entity = new RedisOtpEntity(otp.userId(), otp.code(), otp.expiresAt(), otp.attemptsLeft());
        // Si la clé existe déjà, on ne modifie pas le TTL
        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            redisTemplate.opsForValue().set(key, entity); // remplace la valeur mais garde le TTL
        } else {
            redisTemplate.opsForValue().set(key, entity, Duration.ofSeconds(ttlSeconds));
        }
    }

    @Override
    public Optional<Otp> findByUserId(String userId) {
        return Stream.ofNullable(redisTemplate.opsForValue().get("otp:" + userId))
                .findFirst()
                .map(e -> new Otp(e.userId(), e.code(), e.expiresAt(), e.attemptsLeft()));
    }

    @Override
    public void delete(String userId) {
        redisTemplate.delete("otp:" + userId);
    }
}
