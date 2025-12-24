package dev.stephyu.mfa.domain;

import java.time.Instant;
import java.util.Objects;

public class Otp {

    private final String userId;
    private final String code;
    private final Instant expiresAt;
    private int attemptsLeft;

    public Otp(String userId, String code, Instant expiresAt, int attemptsLeft) {
        this.userId = Objects.requireNonNull(userId, "userId");
        this.code = Objects.requireNonNull(code, "code");
        this.expiresAt = Objects.requireNonNull(expiresAt, "expiresAt");
        this.attemptsLeft = attemptsLeft;
    }

    public String userId() {
        return userId;
    }

    public String code() {
        return code;
    }

    public Instant expiresAt() {
        return expiresAt;
    }

    public int attemptsLeft() {
        return attemptsLeft;
    }

    public boolean isExpired(Instant now) {
        return now.isAfter(expiresAt);
    }

    /**
     * Validate provided code. This method mutates attemptsLeft on failure.
     * Returns true if code matches and not expired.
     */
    public boolean validate(String providedCode, Instant now) {
        if (isExpired(now)) {
            return false;
        }
        if (this.code.equals(providedCode)) {
            return true;
        }
        // wrong code -> decrement attempts
        attemptsLeft = attemptsLeft - 1;
        return false;
    }

}
