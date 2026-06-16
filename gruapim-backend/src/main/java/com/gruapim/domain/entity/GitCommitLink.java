package com.gruapim.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "git_commit_links",
       indexes = {
           @Index(name = "idx_gcl_task_id", columnList = "task_id"),
           @Index(name = "idx_gcl_commit_hash", columnList = "commit_hash")
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class GitCommitLink {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repository_id", nullable = false)
    private GitRepository repository;

    @Column(nullable = false, length = 40)
    private String commitHash;

    @Column(columnDefinition = "TEXT")
    private String commitMessage;

    @Column(length = 255)
    private String branchName;

    @Column(nullable = false)
    private Instant committedAt;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private Instant linkedAt;
}
