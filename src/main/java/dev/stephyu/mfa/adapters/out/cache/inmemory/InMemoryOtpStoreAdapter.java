package dev.stephyu.mfa.adapters.out.cache.inmemory;

import dev.stephyu.mfa.domain.Otp;
import dev.stephyu.mfa.ports.out.OtpStorePort;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Profile("inmemory")
@Component
public class InMemoryOtpStoreAdapter implements OtpStorePort {

    private final Map<String, Stored> map = new ConcurrentHashMap<>();

    @Override
    public void save(Otp otp, long ttlSeconds) {
        Instant expires = Instant.now().plusSeconds(ttlSeconds);
        map.put(otp.userId(), new Stored(otp, expires));
        System.out.println("IN_MEMORY : Save for " + otp.userId() + ":" + otp);
    }

    @Override
    public Optional<Otp> findByUserId(String userId) {
        Stored s = map.get(userId);
        if (s == null) return Optional.empty();
        if (Instant.now().isAfter(s.expires)) {
            map.remove(userId);
            return Optional.empty();
        }
        return Optional.of(s.otp);
    }

    @Override
    public void delete(String userId) {
        map.remove(userId);
    }

    private record Stored(Otp otp, Instant expires) {
    }
}

