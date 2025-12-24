package dev.stephyu.mfa.application.usecase;

import dev.stephyu.mfa.application.dto.ValidateOtpRequest;
import dev.stephyu.mfa.domain.Otp;
import dev.stephyu.mfa.ports.out.OtpStore;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;

public class ValidateOtpUseCase {

    private final OtpStore otpStore;
    private final Clock clock;

    public ValidateOtpUseCase(OtpStore otpStore, Clock clock) {
        this.otpStore = otpStore;
        this.clock = clock;
    }

    // convenience constructor for production code: uses system UTC clock
    public ValidateOtpUseCase(OtpStore otpStore) {
        this(otpStore, Clock.systemUTC());
    }

    public boolean validate(ValidateOtpRequest request) {
        Optional<Otp> maybe = otpStore.findByUserId(request.userId());
        Instant now = Instant.now(clock);
        if (maybe.isEmpty()) {
            return false;
        }
        Otp otp = maybe.get();
        boolean ok = otp.validate(request.code(), now);
        if (ok) {
            // one-time usage
            otpStore.delete(request.userId());
            return true;
        }
        // if attempts exhausted -> delete
        if (otp.attemptsLeft() <= 0) {
            otpStore.delete(request.userId());
        } else {
            // save updated attempts back with remaining TTL estimated as seconds until expiry
            long remaining = otp.expiresAt().getEpochSecond() - now.getEpochSecond();
            if (remaining > 0) {
                otpStore.save(otp, remaining);
            } else {
                otpStore.delete(request.userId());
            }
        }
        return false;
    }
}

