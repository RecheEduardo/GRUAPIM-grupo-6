package com.gruapim.collaboration.dto.response;

import com.gruapim.collaboration.domain.entity.Notification;
import com.gruapim.collaboration.domain.enums.NotificationType;

import java.time.Instant;
import java.util.UUID;

public record NotificationResponse(
        UUID id,
        UUID userId,
        NotificationType type,
        String title,
        String message,
        boolean read,
        String relatedEntityType,
        UUID relatedEntityId,
        Instant createdAt
) {
    public static NotificationResponse from(Notification n) {
        return new NotificationResponse(
                n.getId(), n.getUserId(), n.getType(),
                n.getTitle(), n.getMessage(), n.isRead(),
                n.getRelatedEntityType(), n.getRelatedEntityId(),
                n.getCreatedAt()
        );
    }
}
