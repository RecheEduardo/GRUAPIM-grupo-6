package com.gruapim.dto.response;

import com.gruapim.domain.entity.ProjectMember;
import com.gruapim.domain.enums.UserRole;

import java.time.Instant;
import java.util.UUID;

public record ProjectMemberResponse(
        UUID id,
        UUID projectId,
        UUID userId,
        String userName,
        String userEmail,
        UserRole role,
        Instant joinedAt
) {
    public static ProjectMemberResponse from(ProjectMember pm) {
        return new ProjectMemberResponse(
                pm.getId(), pm.getProject().getId(),
                pm.getUser().getId(), pm.getUser().getName(), pm.getUser().getEmail(),
                pm.getRole(), pm.getJoinedAt()
        );
    }
}
