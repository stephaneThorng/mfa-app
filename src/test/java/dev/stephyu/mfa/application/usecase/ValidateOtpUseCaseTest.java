package dev.stephyu.mfa.application.usecase;

import dev.stephyu.mfa.application.dto.ValidateOtpRequest;
import dev.stephyu.mfa.domain.Otp;
import dev.stephyu.mfa.ports.out.OtpStore;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ValidateOtpUseCaseTest {

    private static final Instant FIXED_NOW = Instant.parse("2025-01-01T10:00:00Z");

    static class InMemoryStore implements OtpStore {
        final Map<String, Otp> map = new HashMap<>();
        Otp lastSaved;
        long lastTtl;
        String lastDeleted;

        @Override
        public void save(Otp otp, long ttlSeconds) {
            lastSaved = otp;
            lastTtl = ttlSeconds;
            map.put(otp.userId(), otp);
        }

        @Override
        public void delete(String userId) {
            lastDeleted = userId;
            map.remove(userId);
        }

        @Override
        public Optional<Otp> findByUserId(String userId) {
            return Optional.ofNullable(map.get(userId));
        }
    }

    @Test
    public void validate_returnsTrue_andDeletes_onCorrectCode() {
        InMemoryStore store = new InMemoryStore();
        Clock clock = Clock.fixed(FIXED_NOW, ZoneOffset.UTC);
        ValidateOtpUseCase uc = new ValidateOtpUseCase(store, clock);

        Otp otp = new Otp("u1", "1234", FIXED_NOW.plusSeconds(30), 3);
        store.map.put("u1", otp);

        boolean ok = uc.validate(new ValidateOtpRequest("u1", "1234"));

        assertTrue(ok);
        assertEquals("u1", store.lastDeleted, "successful validation should delete OTP");
    }

    @Test
    public void validate_decrementsAttempts_andSaves_whenWrongButNotExhausted() {
        InMemoryStore store = new InMemoryStore();
        Clock clock = Clock.fixed(FIXED_NOW, ZoneOffset.UTC);
        ValidateOtpUseCase uc = new ValidateOtpUseCase(store, clock);

        Otp otp = new Otp("u2", "9999", FIXED_NOW.plusSeconds(30), 3);
        store.map.put("u2", otp);

        boolean ok = uc.validate(new ValidateOtpRequest("u2", "0000"));

        assertFalse(ok);
        assertNull(store.lastDeleted, "should not delete when attempts remain");
        assertNotNull(store.lastSaved, "should save updated otp when attempts remain");

        assertTrue(store.findByUserId("u2").isPresent());
        // TTL should be seconds until expiry (30)
        assertEquals(30, store.lastTtl);
    }

    @Test
    public void validate_deletes_whenAttemptsExhausted_afterWrong() {
        InMemoryStore store = new InMemoryStore();
        Clock clock = Clock.fixed(FIXED_NOW, ZoneOffset.UTC);
        ValidateOtpUseCase uc = new ValidateOtpUseCase(store, clock);

        Otp otp = new Otp("u3", "9999", FIXED_NOW.plusSeconds(30), 1);
        store.map.put("u3", otp);

        boolean ok = uc.validate(new ValidateOtpRequest("u3", "0000"));

        assertFalse(ok);
        assertEquals("u3", store.lastDeleted, "should delete when attempts exhausted");

        assertNull(store.lastSaved, "should not save when deleting");
    }

    @Test
    public void validate_returnsFalse_andDeletes_whenExpired() {
        InMemoryStore store = new InMemoryStore();
        Clock clock = Clock.fixed(FIXED_NOW, ZoneOffset.UTC);
        ValidateOtpUseCase uc = new ValidateOtpUseCase(store, clock);

        Otp otp = new Otp("u4", "abcd", FIXED_NOW.minusSeconds(1), 3);
        store.map.put("u4", otp);

        boolean ok = uc.validate(new ValidateOtpRequest("u4", "abcd"));

        assertFalse(ok);
        // expired -> remaining <= 0 leads to delete in use case
        assertEquals("u4", store.lastDeleted);
    }

    @Test
    public void validate_returnsFalse_whenOtpMissing() {
        InMemoryStore store = new InMemoryStore();
        Clock clock = Clock.fixed(FIXED_NOW, ZoneOffset.UTC);
        ValidateOtpUseCase uc = new ValidateOtpUseCase(store, clock);

        boolean ok = uc.validate(new ValidateOtpRequest("missing", "x"));

        assertFalse(ok);
        assertNull(store.lastDeleted);
        assertNull(store.lastSaved);
    }
}
