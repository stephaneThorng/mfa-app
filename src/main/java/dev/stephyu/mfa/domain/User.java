package dev.stephyu.mfa.domain;

import lombok.Data;

@Data
public class User {
    private final String email;
    private final boolean mfaEnabled;
}
