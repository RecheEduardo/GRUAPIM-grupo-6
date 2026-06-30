package com.gruapim.collaboration.dto.response;

import com.gruapim.collaboration.domain.entity.MeetingNote;
import com.gruapim.collaboration.domain.enums.MeetingType;

import java.time.Instant;
import java.util.UUID;

public record MeetingNoteResponse(
        UUID id,
        UUID sprintId,
        UUID projectId,
        MeetingType type,
        String discussedPoints,
        String decisions,
        String improvementActions,
        UUID createdById,
        String createdByName,
        Instant createdAt,
        Instant updatedAt
) {
    public static MeetingNoteResponse from(MeetingNote mn) {
        return new MeetingNoteResponse(
                mn.getId(), mn.getSprintId(), mn.getProjectId(),
                mn.getType(), mn.getDiscussedPoints(),
                mn.getDecisions(), mn.getImprovementActions(),
                mn.getCreatedById(), mn.getCreatedByName(),
                mn.getCreatedAt(), mn.getUpdatedAt()
        );
    }
}
