package com.gruapim.collaboration.service;

import com.gruapim.collaboration.domain.entity.Notification;
import com.gruapim.collaboration.domain.enums.NotificationType;
import com.gruapim.collaboration.dto.response.NotificationResponse;
import com.gruapim.collaboration.exception.ResourceNotFoundException;
import com.gruapim.collaboration.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public NotificationResponse send(UUID userId, NotificationType type, String title,
                                     String message, String relatedEntityType, UUID relatedEntityId) {
        Notification notification = Notification.builder()
                .userId(userId)
                .type(type)
                .title(title)
                .message(message)
                .relatedEntityType(relatedEntityType)
                .relatedEntityId(relatedEntityId)
                .build();

        NotificationResponse response = NotificationResponse.from(notificationRepository.save(notification));

        // Push em tempo real — cliente subscreve /topic/user/{userId}/notifications via STOMP.
        messagingTemplate.convertAndSend("/topic/user/" + userId + "/notifications", response);

        return response;
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> listForUser(UUID userId) {
        return notificationRepository
                .findByUserIdOrderByCreatedAtDesc(userId, Pageable.ofSize(20))
                .stream().map(NotificationResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> listUnreadForUser(UUID userId) {
        return notificationRepository
                .findByUserIdAndReadFalseOrderByCreatedAtDesc(userId)
                .stream().map(NotificationResponse::from).toList();
    }

    @Transactional
    public void markAsRead(UUID notificationId, UUID requestingUserId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notificação não encontrada"));

        if (!notification.getUserId().equals(requestingUserId)) {
            throw new IllegalArgumentException("Acesso negado");
        }

        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Transactional
    public void markAllAsRead(UUID userId) {
        notificationRepository.markAllAsReadByUserId(userId);
    }
}
