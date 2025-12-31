package dev.stephyu.mfa.application.usecase;

import dev.stephyu.mfa.application.dto.GenerateOtpRequest;
import dev.stephyu.mfa.domain.Otp;
import dev.stephyu.mfa.ports.out.OtpEventPublisherPort;
import dev.stephyu.mfa.ports.out.OtpStorePort;
import dev.stephyu.mfa.ports.out.UserRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GenerateOtpUseCaseTest {
    private static final Instant FIXED_NOW = Instant.parse("2025-01-01T10:00:00Z");

    @Mock
    private UserRepositoryPort userRepositoryPort;

    static class TestStorePort implements OtpStorePort {
        Otp lastSaved;
        long lastTtl;
        String deletedUser;

        @Override
        public void save(Otp otp, long ttlSeconds) {
            this.lastSaved = otp;
            this.lastTtl = ttlSeconds;
        }

        @Override
        public void delete(String userId) {
            this.deletedUser = userId;
        }

        @Override
        public java.util.Optional<Otp> findByUserId(String userId) {
            return java.util.Optional.empty();
        }
    }

    static class TestPublisherPort implements OtpEventPublisherPort {
        Otp lastPublished;

        @Override
        public void publishOtpGenerated(Otp otp) {
            this.lastPublished = otp;
        }
    }

    @Test
    void generate_createsOtp_savesAndPublishes_andUsesTtlAndAttempts() throws Exception {
        TestStorePort store = new TestStorePort();
        TestPublisherPort publisher = new TestPublisherPort();
        Clock clock = Clock.fixed(FIXED_NOW, ZoneOffset.UTC);

        when(userRepositoryPort.findById("user-1")).thenReturn( java.util.Optional.of(new dev.stephyu.mfa.domain.User("user-1", true)) );
        GenerateOtpUseCase uc = new GenerateOtpUseCase(userRepositoryPort, store, publisher, clock, 60, 3);

        // make generated code deterministic by seeding the private Random
        Random seed = new Random(0);
        int expectedInt = seed.nextInt(1_000_000);
        String expectedCode = String.format("%06d", expectedInt);

        Field randomField = GenerateOtpUseCase.class.getDeclaredField("random");
        randomField.setAccessible(true);
        randomField.set(uc, new Random(0));

        Otp result = uc.generate(new GenerateOtpRequest("user-1"));

        assertNotNull(result);
        assertEquals("user-1", result.userId());
        assertEquals(3, result.attemptsLeft());
        assertEquals(FIXED_NOW.plusSeconds(60), result.expiresAt());

        // code deterministic check
        assertEquals(expectedCode, result.code());
        // store and publisher were called with the same otp
        assertSame(store.lastSaved, result);
        assertEquals(60, store.lastTtl);
        assertSame(publisher.lastPublished, result);
        assertNull(store.deletedUser);
    }
}
