package com.gruapim.dto.response;

import com.gruapim.domain.entity.GitRepository;

import java.time.Instant;
import java.util.UUID;

public record GitRepoResponse(
        UUID id,
        UUID projectId,
        String repositoryUrl,
        String provider,
        UUID connectedById,
        Instant createdAt
) {
    public static GitRepoResponse from(GitRepository r) {
        return new GitRepoResponse(
                r.getId(), r.getProject().getId(),
                r.getRepositoryUrl(), r.getProvider(),
                r.getConnectedBy().getId(), r.getCreatedAt()
        );
    }
}
