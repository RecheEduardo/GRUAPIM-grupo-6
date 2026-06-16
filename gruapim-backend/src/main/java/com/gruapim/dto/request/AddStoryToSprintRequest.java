package com.gruapim.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record AddStoryToSprintRequest(
        @NotNull UUID storyId
) {}