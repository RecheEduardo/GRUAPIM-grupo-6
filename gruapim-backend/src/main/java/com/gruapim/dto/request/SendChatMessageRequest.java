package com.gruapim.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record SendChatMessageRequest(
        @NotNull UUID projectId,
        @NotBlank String content
) {}