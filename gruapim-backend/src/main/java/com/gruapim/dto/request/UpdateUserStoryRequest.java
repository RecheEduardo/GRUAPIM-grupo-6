package com.gruapim.dto.request;

import com.gruapim.domain.enums.Priority;
import com.gruapim.domain.enums.StoryStatus;
import jakarta.validation.constraints.NotBlank;

public record UpdateUserStoryRequest(
        @NotBlank String title,
        String description,
        Priority priority,
        StoryStatus status,
        Integer storyPoints,
        Integer position
) {}