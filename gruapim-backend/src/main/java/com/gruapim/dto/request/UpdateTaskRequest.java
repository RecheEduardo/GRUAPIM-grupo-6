package com.gruapim.dto.request;

import com.gruapim.domain.enums.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public record UpdateTaskRequest(
        @NotBlank String title,
        String description,
        UUID assigneeId,
        TaskStatus status,
        UUID kanbanColumnId
) {}