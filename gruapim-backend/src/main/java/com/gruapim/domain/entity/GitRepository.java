package com.gruapim.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "git_repositories",
       indexes = @Index(name = "idx_gr_project_id", columnList = "project_id"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class GitRepository {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(nullable = false, length = 500)
    private String repositoryUrl;

    @Column(nullable = false, length = 50)
    private String provider;

    /** Token criptografado com AES-256 pelo GitIntegrationService. */
    @Column(name = "access_token_encrypted", length = 500)
    private String accessTokenEncrypted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "connected_by_id", nullable = false)
    private User connectedBy;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private Instant createdAt;
}
