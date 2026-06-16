package com.gruapim.dto.request;

import com.gruapim.domain.enums.SprintStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record UpdateSprintRequest(
        @NotBlank String name,
        String goal,
        @NotNull LocalDate startDate,
        @NotNull LocalDate endDate,
        SprintStatus status
) {}