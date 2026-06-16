package com.gruapim.dto.response;

import com.gruapim.domain.entity.MeetingNote;
import com.gruapim.domain.enums.MeetingType;
import java.time.Instant;
import java.util.UUID;

public record MeetingNoteResponse(
        UUID id,
        UUID sprintId,
        MeetingType type,
        String discussedPoints,
        String decisions,
        String improvementActions,
        UUID createdById,
        String createdByName,
        Instant createdAt
) {
    public static MeetingNoteResponse from(MeetingNote mn) {
        return new MeetingNoteResponse(
                mn.getId(), mn.getSprint().getId(),
                mn.getType(), mn.getDiscussedPoints(),
                mn.getDecisions(), mn.getImprovementActions(),
                mn.getCreatedBy().getId(), mn.getCreatedBy().getName(),
                mn.getCreatedAt()
        );
    }
}