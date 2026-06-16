package com.gruapim.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CreateKanbanColumnRequest(
        @NotNull UUID projectId,
        @NotBlank String name,
        @NotNull Integer position,
        boolean terminal
) {}