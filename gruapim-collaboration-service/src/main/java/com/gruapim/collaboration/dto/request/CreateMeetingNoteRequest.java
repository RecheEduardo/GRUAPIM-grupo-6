package com.gruapim.collaboration.dto.request;

import com.gruapim.collaboration.domain.enums.MeetingType;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateMeetingNoteRequest(
        @NotNull UUID sprintId,
        @NotNull UUID projectId,
        @NotNull MeetingType type,
        String discussedPoints,
        String decisions,
        String improvementActions
) {}
