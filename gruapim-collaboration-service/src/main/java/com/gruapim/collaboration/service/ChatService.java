package com.gruapim.collaboration.service;

import com.gruapim.collaboration.domain.entity.ChatMessage;
import com.gruapim.collaboration.dto.request.SendChatMessageRequest;
import com.gruapim.collaboration.dto.response.ChatMessageResponse;
import com.gruapim.collaboration.repository.ChatMessageRepository;
import com.gruapim.collaboration.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public ChatMessageResponse send(SendChatMessageRequest request, UserPrincipal sender) {
        ChatMessage message = ChatMessage.builder()
                .projectId(request.projectId())
                .senderId(sender.id())
                .senderName(sender.name())
                .content(request.content())
                .build();

        ChatMessageResponse response = ChatMessageResponse.from(chatMessageRepository.save(message));

        messagingTemplate.convertAndSend(
                "/topic/project/" + request.projectId() + "/chat",
                response
        );

        return response;
    }

    @Transactional(readOnly = true)
    public List<ChatMessageResponse> history(UUID projectId, int limit) {
        return chatMessageRepository
                .findByProjectIdOrderBySentAtDesc(projectId, Pageable.ofSize(limit))
                .stream().map(ChatMessageResponse::from).toList();
    }
}
