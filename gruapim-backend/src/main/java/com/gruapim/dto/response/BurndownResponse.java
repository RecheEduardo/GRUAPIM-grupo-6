package com.gruapim.dto.response;

import java.util.List;
import java.util.UUID;

public record BurndownResponse(
        UUID sprintId,
        String sprintName,
        int totalPoints,
        List<BurndownPointResponse> data
) {}