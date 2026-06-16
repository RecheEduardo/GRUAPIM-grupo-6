package com.gruapim.dto.response;

import com.gruapim.domain.entity.Task;
import com.gruapim.domain.enums.TaskStatus;
import java.time.Instant;
import java.util.UUID;

public record TaskResponse(
        UUID id,
        UUID storyId,
        String storyTitle,
        String title,
        String description,
        UUID assigneeId,
        String assigneeName,
        TaskStatus status,
        UUID kanbanColumnId,
        String kanbanColumnName,
        UUID createdById,
        Instant createdAt,
        Instant updatedAt
) {
    public static TaskResponse from(Task t) {
        return new TaskResponse(
                t.getId(),
                t.getStory().getId(), t.getStory().getTitle(),
                t.getTitle(), t.getDescription(),
                t.getAssignee() != null ? t.getAssignee().getId() : null,
                t.getAssignee() != null ? t.getAssignee().getName() : null,
                t.getStatus(),
                t.getKanbanColumn() != null ? t.getKanbanColumn().getId() : null,
                t.getKanbanColumn() != null ? t.getKanbanColumn().getName() : null,
                t.getCreatedBy().getId(),
                t.getCreatedAt(), t.getUpdatedAt()
        );
    }
}