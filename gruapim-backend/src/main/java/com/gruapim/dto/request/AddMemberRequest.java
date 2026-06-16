package com.gruapim.dto.request;

import com.gruapim.domain.enums.UserRole;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AddMemberRequest(
        @NotNull UUID userId,
        @NotNull UserRole role
) {}
