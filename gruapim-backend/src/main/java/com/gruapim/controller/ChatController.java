package com.gruapim.controller;

import com.gruapim.dto.request.SendChatMessageRequest;
import com.gruapim.dto.response.ChatMessageResponse;
import com.gruapim.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @GetMapping("/api/chat/project/{projectId}/history")
    public ResponseEntity<List<ChatMessageResponse>> history(
            @PathVariable UUID projectId,
            @RequestParam(defaultValue = "50") int limit) {
        return ResponseEntity.ok(chatService.history(projectId, limit));
    }

    // Endpoint WebSocket STOMP: cliente envia para /app/chat.send
    @MessageMapping("/chat.send")
    public void sendMessage(
            @Payload @Valid SendChatMessageRequest request,
            @AuthenticationPrincipal UserDetails principal) {
        chatService.send(request, principal.getUsername());
    }
}