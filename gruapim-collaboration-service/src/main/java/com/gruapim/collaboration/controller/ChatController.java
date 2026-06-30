package com.gruapim.collaboration.controller;

import com.gruapim.collaboration.dto.request.SendChatMessageRequest;
import com.gruapim.collaboration.dto.response.ChatMessageResponse;
import com.gruapim.collaboration.security.UserPrincipal;
import com.gruapim.collaboration.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @GetMapping("/chat/project/{projectId}/history")
    public ResponseEntity<List<ChatMessageResponse>> history(
            @PathVariable UUID projectId,
            @RequestParam(defaultValue = "50") int limit) {
        return ResponseEntity.ok(chatService.history(projectId, limit));
    }

    // Cliente STOMP envia para /app/chat.send; broadcast vai para /topic/project/{id}/chat
    @MessageMapping("/chat.send")
    public void sendMessage(
            @Payload @Valid SendChatMessageRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        chatService.send(request, principal);
    }
}
