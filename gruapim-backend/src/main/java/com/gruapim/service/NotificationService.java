package com.gruapim.service;

import com.gruapim.domain.entity.Notification;
import com.gruapim.domain.entity.User;
import com.gruapim.domain.enums.NotificationType;
import com.gruapim.dto.response.NotificationResponse;
import com.gruapim.repository.NotificationRepository;
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
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public NotificationResponse send(UUID userId, NotificationType type, String title,
                                     String message, String relatedEntityType, UUID relatedEntityId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        Notification notification = Notification.builder()
                .user(user)
                .type(type)
                .title(title)
                .message(message)
                .relatedEntityType(relatedEntityType)
                .relatedEntityId(relatedEntityId)
                .build();

        NotificationResponse response = NotificationResponse.from(notificationRepository.save(notification));

        // Push em tempo real via WebSocket para o usuário específico
        messagingTemplate.convertAndSendToUser(
                user.getEmail(),
                "/queue/notifications",
                response
        );

        return response;
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> listForUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(user.getId(), Pageable.ofSize(20))
                .stream().map(NotificationResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> listUnreadForUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        return notificationRepository.findByUserIdAndReadFalseOrderByCreatedAtDesc(user.getId())
                .stream().map(NotificationResponse::from).toList();
    }

    @Transactional
    public void markAsRead(UUID notificationId, String email) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notificação não encontrada"));

        if (!notification.getUser().getEmail().equals(email)) {
            throw new IllegalArgumentException("Acesso negado");
        }

        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Transactional
    public void markAllAsRead(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        notificationRepository.markAllAsReadByUserId(user.getId());
    }
}