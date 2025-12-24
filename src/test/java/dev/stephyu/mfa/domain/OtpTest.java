package dev.stephyu.mfa.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

public class OtpTest {

    @Test
    public void validate_returnsTrue_whenCodeMatches_andNotExpired() {
        Instant now = Instant.parse("2025-01-01T10:00:00Z");
        Instant expires = now.plusSeconds(60);
        Otp otp = new Otp("user", "1234", expires, 3);

        assertTrue(otp.validate("1234", now));
        assertEquals(3, otp.attemptsLeft(), "attempts should not be decremented on success");
    }

    @Test
    public void validate_decrementsAttempts_andReturnsFalse_onWrongCode_whenNotExpired() {
        Instant now = Instant.parse("2025-01-01T10:00:00Z");
        Instant expires = now.plusSeconds(60);
        Otp otp = new Otp("user", "1234", expires, 3);

        assertFalse(otp.validate("0000", now));
        assertEquals(2, otp.attemptsLeft(), "attempts should decrement once on wrong code");

        assertFalse(otp.validate("0001", now));
        assertEquals(1, otp.attemptsLeft(), "attempts should decrement again on another wrong code");

        assertFalse(otp.validate("0002", now));
        assertEquals(0, otp.attemptsLeft(), "final wrong attempt should bring attempts to zero");
    }

    @Test
    public void validate_returnsFalse_andDoesNotDecrement_whenExpired() {
        Instant now = Instant.parse("2025-01-01T10:00:00Z");
        Instant expires = now.minusSeconds(1); // already expired
        Otp otp = new Otp("user", "1234", expires, 3);

        assertFalse(otp.validate("1234", now), "expired OTP must return false even for correct code");
        assertEquals(3, otp.attemptsLeft(), "attempts must not change when expired");
    }

    @Test
    public void isExpired_behaviour_atAndAfterBoundary() {
        Instant expires = Instant.parse("2025-01-01T10:00:00Z");
        Otp otp = new Otp("user", "1234", expires, 1);

        assertFalse(otp.isExpired(expires), "OTP should not be expired when now == expiresAt");
        assertTrue(otp.isExpired(expires.plusMillis(1)), "OTP should be expired when now > expiresAt");
    }

    @Test
    public void accessors_returnValues() {
        Instant now = Instant.parse("2025-01-01T10:00:00Z");
        Otp otp = new Otp("uid", "code", now.plusSeconds(10), 5);

        assertEquals("uid", otp.userId());
        assertEquals("code", otp.code());
        assertEquals(5, otp.attemptsLeft());
        assertEquals(now.plusSeconds(10), otp.expiresAt());
    }
}
