package com.gruapim.dto.response;

import com.gruapim.domain.entity.Project;

import java.time.Instant;
import java.util.UUID;

public record ProjectResponse(
        UUID id,
        String name,
        String description,
        UUID createdById,
        String createdByName,
        Instant createdAt,
        Instant updatedAt
) {
    public static ProjectResponse from(Project p) {
        return new ProjectResponse(
                p.getId(), p.getName(), p.getDescription(),
                p.getCreatedBy().getId(), p.getCreatedBy().getName(),
                p.getCreatedAt(), p.getUpdatedAt()
        );
    }
}
