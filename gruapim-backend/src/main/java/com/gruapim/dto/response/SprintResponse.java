package com.gruapim.dto.response;

import com.gruapim.domain.entity.Sprint;
import com.gruapim.domain.enums.SprintStatus;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record SprintResponse(
        UUID id,
        UUID projectId,
        String name,
        String goal,
        LocalDate startDate,
        LocalDate endDate,
        SprintStatus status,
        UUID createdById,
        Instant createdAt
) {
    public static SprintResponse from(Sprint s) {
        return new SprintResponse(
                s.getId(), s.getProject().getId(),
                s.getName(), s.getGoal(),
                s.getStartDate(), s.getEndDate(),
                s.getStatus(),
                s.getCreatedBy().getId(), s.getCreatedAt()
        );
    }
}