package dev.stephyu.mfa.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class OtpTest {

    @Test
    void validate_returnsTrue_whenCodeMatches_andNotExpired() {
        Instant now = Instant.parse("2025-01-01T10:00:00Z");
        Instant expires = now.plusSeconds(60);
        Otp otp = new Otp("user", "1234", expires, 3);

        Otp.ValidationStatus validate = otp.validate("1234", now);
        assertInstanceOf(Otp.ValidationStatus.Valid.class, validate, "validation should return Valid outcome for correct code");

        Otp.ValidationStatus.Valid valid = (Otp.ValidationStatus.Valid) validate;
        assertEquals(3, valid.otp().attemptsLeft(), "attempts should not be decremented on success");

    }

    @Test
    void validate_decrementsAttempts_andReturnsInvalid_onWrongCode_whenNotExpired() {
        Instant now = Instant.parse("2025-01-01T10:00:00Z");
        Instant expires = now.plusSeconds(60);
        Otp otp = new Otp("user", "1234", expires, 3);

        Otp.ValidationStatus validate1 = otp.validate("0000", now);
        assertInstanceOf(Otp.ValidationStatus.Invalid.class, validate1);
        Otp.ValidationStatus.Invalid invalid1 = (Otp.ValidationStatus.Invalid) validate1;
        assertEquals(2, invalid1.otp().attemptsLeft(), "attempts should decrement once on wrong code");

        Otp.ValidationStatus validate2 = invalid1.otp().validate("0001", now);
        assertInstanceOf(Otp.ValidationStatus.Invalid.class, validate2);
        Otp.ValidationStatus.Invalid invalid2 = (Otp.ValidationStatus.Invalid) validate2;
        assertEquals(1, invalid2.otp().attemptsLeft(), "attempts should decrement again on another wrong code");

        Otp.ValidationStatus validate3 = invalid2.otp().validate("0002", now);
        assertInstanceOf(Otp.ValidationStatus.AttemptsExhausted.class, validate3);
        Otp.ValidationStatus.AttemptsExhausted exhausted = (Otp.ValidationStatus.AttemptsExhausted) validate3;
        assertEquals(0, exhausted.otp().attemptsLeft(), "final wrong attempt should bring attempts to zero");
    }

    @Test
    void validate_returnsExpired_andDoesNotDecrement_whenExpired() {
        Instant now = Instant.parse("2025-01-01T10:00:00Z");
        Instant expires = now.minusSeconds(1); // already expired
        Otp otp = new Otp("user", "1234", expires, 3);

        Otp.ValidationStatus validate = otp.validate("1234", now);
        assertInstanceOf(Otp.ValidationStatus.Expired.class, validate, "expired OTP must return Expired outcome even for correct code");
        Otp.ValidationStatus.Expired expired = (Otp.ValidationStatus.Expired) validate;
        assertEquals(3, expired.otp().attemptsLeft(), "attempts must not change when expired");
    }

    @Test
    void isExpired_behaviour_atAndAfterBoundary() {
        Instant expires = Instant.parse("2025-01-01T10:00:00Z");
        Otp otp = new Otp("user", "1234", expires, 1);

        assertFalse(otp.isExpired(expires), "OTP should not be expired when now == expiresAt");
        assertTrue(otp.isExpired(expires.plusMillis(1)), "OTP should be expired when now > expiresAt");
    }

    @Test
    void accessors_returnValues() {
        Instant now = Instant.parse("2025-01-01T10:00:00Z");
        Otp otp = new Otp("uid", "code", now.plusSeconds(10), 5);

        assertEquals("uid", otp.userId());
        assertEquals("code", otp.code());
        assertEquals(5, otp.attemptsLeft());
        assertEquals(now.plusSeconds(10), otp.expiresAt());
    }
}
