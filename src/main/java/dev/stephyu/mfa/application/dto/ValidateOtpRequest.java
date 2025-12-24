package dev.stephyu.mfa.application.dto;

import java.util.Objects;

public record ValidateOtpRequest(String userId, String code) {
    public ValidateOtpRequest(String userId, String code) {
        this.userId = Objects.requireNonNull(userId);
        this.code = Objects.requireNonNull(code);
    }
}

