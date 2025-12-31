package dev.stephyu.mfa.ports.out;

import dev.stephyu.mfa.domain.Otp;
import java.util.Optional;

public interface OtpStorePort {
    void save(Otp otp, long ttlSeconds);
    Optional<Otp> findByUserId(String userId);
    void delete(String userId);
}

