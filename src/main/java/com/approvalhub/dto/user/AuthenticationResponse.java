package com.approvalhub.dto.user;

public record AuthenticationResponse(
        String token,
        String type,
        long expiresIn
) {
    public AuthenticationResponse(String token, long expiresIn) {
        this(token, "Bearer", expiresIn);
    }
}
