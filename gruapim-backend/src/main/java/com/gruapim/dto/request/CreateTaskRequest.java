package com.gruapim.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CreateTaskRequest(
        @NotNull UUID storyId,
        @NotBlank String title,
        String description,
        UUID assigneeId,
        UUID kanbanColumnId
) {}