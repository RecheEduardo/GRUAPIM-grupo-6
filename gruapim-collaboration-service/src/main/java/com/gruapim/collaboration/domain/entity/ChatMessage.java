package com.gruapim.collaboration.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "chat_messages", indexes = {
    @Index(name = "idx_cm_project_id", columnList = "project_id"),
    @Index(name = "idx_cm_sent_at",    columnList = "sent_at")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@EntityListeners(AuditingEntityListener.class)
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    /** Desnormalizado: referência lógica ao projeto, sem FK cruzando fronteira de serviço. */
    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    /** Desnormalizado: id do remetente, populado a partir das claims JWT. */
    @Column(name = "sender_id", nullable = false)
    private UUID senderId;

    @Column(name = "sender_name", nullable = false)
    private String senderName;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private Instant sentAt;
}
