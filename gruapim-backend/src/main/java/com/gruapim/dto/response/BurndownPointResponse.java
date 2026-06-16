package com.gruapim.dto.response;

import java.time.LocalDate;

public record BurndownPointResponse(
        LocalDate date,
        int remainingPoints,
        int idealPoints
) {}