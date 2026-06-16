package com.gruapim.dto.request;

import com.gruapim.domain.enums.MeetingType;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CreateMeetingNoteRequest(
        @NotNull UUID sprintId,
        @NotNull MeetingType type,
        String discussedPoints,
        String decisions,
        String improvementActions
) {}