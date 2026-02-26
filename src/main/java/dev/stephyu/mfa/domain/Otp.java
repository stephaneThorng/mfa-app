package dev.stephyu.mfa.domain;

import java.time.Instant;

public record Otp(String userId, String code, Instant expiresAt, int attemptsLeft) {

    public boolean isExpired(Instant now) {
        return now.isAfter(expiresAt);
    }

    /**
     * Validate provided code.
     * Returns ValidationOutcome : validation status  and  updated Otp.
     */
    public ValidationStatus validate(String providedCode, Instant now) {
        if (isExpired(now)) {
            return new ValidationStatus.Expired(this);
        }
        if (this.code.equals(providedCode)) {
            return new ValidationStatus.Valid(this);
        }
        int newAttempts = attemptsLeft - 1;
        if (newAttempts <= 0) {
            return new ValidationStatus.AttemptsExhausted(new Otp(userId, code, expiresAt, 0));
        }
        return new ValidationStatus.Invalid(new Otp(userId, code, expiresAt, newAttempts));
    }

    public sealed interface ValidationStatus {
        record Valid(Otp otp) implements ValidationStatus {}

        record Expired(Otp otp) implements ValidationStatus {}

        record Invalid(Otp otp) implements ValidationStatus {}

        record AttemptsExhausted(Otp otp) implements ValidationStatus {}
    }

}
