package com.gruapim.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

public record LinkCommitRequest(
        @NotNull UUID taskId,
        @NotNull UUID repositoryId,
        @NotBlank String commitHash,
        String commitMessage,
        String branchName,
        @NotNull Instant committedAt
) {}
