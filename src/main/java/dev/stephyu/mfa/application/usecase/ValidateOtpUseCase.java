package dev.stephyu.mfa.application.usecase;

import dev.stephyu.mfa.application.dto.ValidateOtpRequest;
import dev.stephyu.mfa.domain.Otp;
import dev.stephyu.mfa.ports.out.OtpStorePort;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;

public class ValidateOtpUseCase {

    private final OtpStorePort otpStorePort;
    private final Clock clock;

    public ValidateOtpUseCase(OtpStorePort otpStorePort, Clock clock) {
        this.otpStorePort = otpStorePort;
        this.clock = clock;
    }

    // convenience constructor for production code: uses system UTC clock
    public ValidateOtpUseCase(OtpStorePort otpStorePort) {
        this(otpStorePort, Clock.systemUTC());
    }

    public boolean validate(ValidateOtpRequest request) {
        Optional<Otp> maybe = otpStorePort.findByUserId(request.userId());
        Instant now = Instant.now(clock);
        if (maybe.isEmpty()) {
            return false;
        }
        Otp otp = maybe.get();
        boolean ok = otp.validate(request.code(), now);
        if (ok) {
            // one-time usage
            otpStorePort.delete(request.userId());
            return true;
        }
        // if attempts exhausted -> delete
        if (otp.attemptsLeft() <= 0) {
            otpStorePort.delete(request.userId());
        } else {
            // save updated attempts back with remaining TTL estimated as seconds until expiry
            long remaining = otp.expiresAt().getEpochSecond() - now.getEpochSecond();
            if (remaining > 0) {
                otpStorePort.save(otp, remaining);
            } else {
                otpStorePort.delete(request.userId());
            }
        }
        return false;
    }
}

