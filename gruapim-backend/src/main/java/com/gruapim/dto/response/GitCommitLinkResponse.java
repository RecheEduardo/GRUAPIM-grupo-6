package com.gruapim.dto.response;

import com.gruapim.domain.entity.GitCommitLink;

import java.time.Instant;
import java.util.UUID;

public record GitCommitLinkResponse(
        UUID id,
        UUID taskId,
        UUID repositoryId,
        String commitHash,
        String commitMessage,
        String branchName,
        Instant committedAt,
        Instant linkedAt
) {
    public static GitCommitLinkResponse from(GitCommitLink c) {
        return new GitCommitLinkResponse(
                c.getId(), c.getTask().getId(), c.getRepository().getId(),
                c.getCommitHash(), c.getCommitMessage(), c.getBranchName(),
                c.getCommittedAt(), c.getLinkedAt()
        );
    }
}
