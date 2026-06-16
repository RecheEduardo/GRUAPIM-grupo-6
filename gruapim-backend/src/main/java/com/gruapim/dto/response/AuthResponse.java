package com.gruapim.dto.response;

import com.gruapim.domain.enums.UserRole;

import java.util.UUID;

public record AuthResponse(
        String token,
        String tokenType,
        UUID userId,
        String name,
        String email,
        UserRole role
) {
    public AuthResponse(String token, UUID userId, String name, String email, UserRole role) {
        this(token, "Bearer", userId, name, email, role);
    }
}
