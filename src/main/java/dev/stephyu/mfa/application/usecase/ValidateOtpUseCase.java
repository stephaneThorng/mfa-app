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
        if (maybe.isEmpty()) {
            return false;
        }
        Otp otp = maybe.get();
        Instant now = Instant.now(clock);
        Otp.ValidationStatus validate = otp.validate(request.code(), now);

        return switch (validate) {
            case Otp.ValidationStatus.Valid(_) -> {
                otpStorePort.delete(request.userId());
                yield true;
            }
            case Otp.ValidationStatus.AttemptsExhausted(_), Otp.ValidationStatus.Expired(_) -> {
                otpStorePort.delete(request.userId());
                yield false;
            }
            case Otp.ValidationStatus.Invalid(Otp updated) -> {
                long remainingTtl = updated.expiresAt().getEpochSecond() - now.getEpochSecond();
                if (remainingTtl > 0) {
                    otpStorePort.save(updated, remainingTtl);
                } else {
                    otpStorePort.delete(request.userId());
                }
                yield false;
            }
        };
    }

}

