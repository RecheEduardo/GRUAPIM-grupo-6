package com.gruapim.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;

public record CreateSprintRequest(
        @NotNull UUID projectId,
        @NotBlank String name,
        String goal,
        @NotNull LocalDate startDate,
        @NotNull LocalDate endDate
) {}