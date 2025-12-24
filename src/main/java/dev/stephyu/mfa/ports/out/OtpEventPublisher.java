package dev.stephyu.mfa.ports.out;

import dev.stephyu.mfa.domain.Otp;

public interface OtpEventPublisher {
    void publishOtpGenerated(Otp otp);
}

