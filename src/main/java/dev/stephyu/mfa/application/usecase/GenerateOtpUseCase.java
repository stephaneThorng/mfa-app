package dev.stephyu.mfa.application.usecase;

import dev.stephyu.mfa.application.dto.GenerateOtpRequest;
import dev.stephyu.mfa.domain.Otp;
import dev.stephyu.mfa.domain.User;
import dev.stephyu.mfa.ports.out.OtpEventPublisherPort;
import dev.stephyu.mfa.ports.out.OtpStorePort;
import dev.stephyu.mfa.ports.out.UserRepositoryPort;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;

public class GenerateOtpUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final OtpStorePort otpStorePort;
    private final OtpEventPublisherPort publisher;
    private final Clock clock;
    private final Random random = new Random();
    private final int ttlSeconds;
    private final int attempts;

    public GenerateOtpUseCase(UserRepositoryPort userRepositoryPort, OtpStorePort otpStorePort, OtpEventPublisherPort publisher, Clock clock, int ttlSeconds, int attempts) {
        this.userRepositoryPort = userRepositoryPort;
        this.otpStorePort = otpStorePort;
        this.publisher = publisher;
        this.clock = clock;
        this.ttlSeconds = ttlSeconds;
        this.attempts = attempts;
    }

    public Otp generate(GenerateOtpRequest request) {
        String email = request.userId();
        User user = userRepositoryPort.findById(email)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        if (!user.isMfaEnabled()) {
            throw new IllegalStateException("MFA not enabled for user");
        }
        String code = String.format("%06d", random.nextInt(1_000_000));
        Instant expires = Instant.now(clock).plus(ttlSeconds, ChronoUnit.SECONDS);
        Otp otp = new Otp(email, code, expires, attempts);

        otpStorePort.save(otp, ttlSeconds);
        publisher.publishOtpGenerated(otp);
        return otp;
    }
}

