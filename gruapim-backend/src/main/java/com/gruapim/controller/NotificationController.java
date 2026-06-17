package com.gruapim.controller;

import com.gruapim.dto.response.NotificationResponse;
import com.gruapim.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> listAll(
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(notificationService.listForUser(principal.getUsername()));
    }

    @GetMapping("/unread")
    public ResponseEntity<List<NotificationResponse>> listUnread(
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(notificationService.listUnreadForUser(principal.getUsername()));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails principal) {
        notificationService.markAsRead(id, principal.getUsername());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(
            @AuthenticationPrincipal UserDetails principal) {
        notificationService.markAllAsRead(principal.getUsername());
        return ResponseEntity.noContent().build();
    }
}