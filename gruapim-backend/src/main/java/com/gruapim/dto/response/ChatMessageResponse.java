package com.gruapim.dto.response;

import com.gruapim.domain.entity.ChatMessage;
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
                msg.getId(), msg.getProject().getId(),
                msg.getSender().getId(), msg.getSender().getName(),
                msg.getContent(), msg.getSentAt()
        );
    }
}