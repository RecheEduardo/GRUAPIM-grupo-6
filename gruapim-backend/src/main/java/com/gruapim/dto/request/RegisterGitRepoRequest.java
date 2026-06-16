package com.gruapim.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record RegisterGitRepoRequest(
        @NotNull UUID projectId,
        @NotBlank @Size(max = 500) String repositoryUrl,
        @NotBlank @Size(max = 50) String provider,
        String accessToken
) {}
