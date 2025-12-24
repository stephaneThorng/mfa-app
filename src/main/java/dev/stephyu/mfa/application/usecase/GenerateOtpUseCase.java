package dev.stephyu.mfa.application.usecase;

import dev.stephyu.mfa.application.dto.GenerateOtpRequest;
import dev.stephyu.mfa.domain.Otp;
import dev.stephyu.mfa.ports.out.OtpEventPublisher;
import dev.stephyu.mfa.ports.out.OtpStore;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Random;

public class GenerateOtpUseCase {

    private final OtpStore otpStore;
    private final OtpEventPublisher publisher;
    private final Clock clock;
    private final Random random = new Random();
    private final int ttlSeconds;
    private final int attempts;

    public GenerateOtpUseCase(OtpStore otpStore, OtpEventPublisher publisher, Clock clock, int ttlSeconds, int attempts) {
        this.otpStore = otpStore;
        this.publisher = publisher;
        this.clock = clock;
        this.ttlSeconds = ttlSeconds;
        this.attempts = attempts;
    }

    public Otp generate(GenerateOtpRequest request) {
        String code = String.format("%06d", random.nextInt(1_000_000));
        Instant expires = Instant.now(clock).plus(ttlSeconds, ChronoUnit.SECONDS);
        Otp otp = new Otp(request.userId(), code, expires, attempts);

        otpStore.save(otp, ttlSeconds);
        publisher.publishOtpGenerated(otp);
        return otp;
    }
}

