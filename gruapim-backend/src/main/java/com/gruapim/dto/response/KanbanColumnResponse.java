package com.gruapim.dto.response;

import com.gruapim.domain.entity.KanbanColumn;
import java.util.UUID;

public record KanbanColumnResponse(
        UUID id,
        UUID projectId,
        String name,
        Integer position,
        boolean terminal
) {
    public static KanbanColumnResponse from(KanbanColumn col) {
        return new KanbanColumnResponse(
                col.getId(), col.getProject().getId(),
                col.getName(), col.getPosition(), col.isTerminal()
        );
    }
}