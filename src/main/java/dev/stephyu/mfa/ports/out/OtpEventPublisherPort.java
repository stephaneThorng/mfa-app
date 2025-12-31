package dev.stephyu.mfa.ports.out;

import dev.stephyu.mfa.domain.Otp;

public interface OtpEventPublisherPort {
    void publishOtpGenerated(Otp otp);
}

