package com.gruapim.collaboration.dto.response;

import com.gruapim.collaboration.domain.entity.ChatMessage;

import java.time.Instant;
import java.util.UUID;

public record ChatMessageResponse(
        UUID id,
        UUID projectId,
        UUID senderId,
        String senderName,
        String content,
        Instant sentAt
) {
    public static ChatMessageResponse from(ChatMessage msg) {
        return new ChatMessageResponse(
                msg.getId(), msg.getProjectId(),
                msg.getSenderId(), msg.getSenderName(),
                msg.getContent(), msg.getSentAt()
        );
    }
}
