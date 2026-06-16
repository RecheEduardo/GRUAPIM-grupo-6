package com.gruapim.dto.request;

import com.gruapim.domain.enums.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CreateUserStoryRequest(
        @NotNull UUID projectId,
        @NotBlank String title,
        String description,
        Priority priority,
        Integer storyPoints
) {}