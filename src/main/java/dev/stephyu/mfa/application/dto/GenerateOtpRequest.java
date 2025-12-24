package dev.stephyu.mfa.application.dto;

import java.util.Objects;

public record GenerateOtpRequest(String userId) {
    public GenerateOtpRequest(String userId) {
        this.userId = Objects.requireNonNull(userId);
    }
}

