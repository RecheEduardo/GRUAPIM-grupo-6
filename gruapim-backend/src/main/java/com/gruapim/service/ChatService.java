package com.gruapim.service;

import com.gruapim.domain.entity.ChatMessage;
import com.gruapim.domain.entity.Project;
import com.gruapim.domain.entity.User;
import com.gruapim.dto.request.SendChatMessageRequest;
import com.gruapim.dto.response.ChatMessageResponse;
import com.gruapim.repository.ChatMessageRepository;
import com.gruapim.repository.ProjectRepository;
import com.gruapim.repository.UserRepository;
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
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public ChatMessageResponse send(SendChatMessageRequest request, String senderEmail) {
        Project project = projectRepository.findById(request.projectId())
                .orElseThrow(() -> new IllegalArgumentException("Projeto não encontrado"));
        User sender = userRepository.findByEmail(senderEmail)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        ChatMessage message = ChatMessage.builder()
                .project(project)
                .sender(sender)
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
        return chatMessageRepository.findByProjectIdOrderBySentAtDesc(projectId, Pageable.ofSize(limit))
                .stream().map(ChatMessageResponse::from).toList();
    }
}