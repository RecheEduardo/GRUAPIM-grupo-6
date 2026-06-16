package com.gruapim.dto.response;

import com.gruapim.domain.entity.UserStory;
import com.gruapim.domain.enums.Priority;
import com.gruapim.domain.enums.StoryStatus;
import java.time.Instant;
import java.util.UUID;

public record UserStoryResponse(
        UUID id,
        UUID projectId,
        String title,
        String description,
        Priority priority,
        StoryStatus status,
        Integer storyPoints,
        Integer position,
        UUID createdById,
        String createdByName,
        Instant createdAt,
        Instant updatedAt
) {
    public static UserStoryResponse from(UserStory us) {
        return new UserStoryResponse(
                us.getId(), us.getProject().getId(),
                us.getTitle(), us.getDescription(),
                us.getPriority(), us.getStatus(),
                us.getStoryPoints(), us.getPosition(),
                us.getCreatedBy().getId(), us.getCreatedBy().getName(),
                us.getCreatedAt(), us.getUpdatedAt()
        );
    }
}