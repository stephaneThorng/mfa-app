package dev.stephyu.mfa.adapters.out.cache.redis;

import java.time.Instant;

public record RedisOtpEntity(
        String userId,
        String code,
        Instant expiresAt,
        int attemptsLeft
) {}
