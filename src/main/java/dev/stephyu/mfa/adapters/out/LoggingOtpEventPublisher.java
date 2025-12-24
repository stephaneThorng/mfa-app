package dev.stephyu.mfa.adapters.out;

import dev.stephyu.mfa.domain.Otp;
import dev.stephyu.mfa.ports.out.OtpEventPublisher;

public class LoggingOtpEventPublisher implements OtpEventPublisher {

    @Override
    public void publishOtpGenerated(Otp otp) {
        System.out.println("[EVENT] OtpGenerated for user=" + otp.userId() + " code=" + otp.code());
    }
}

