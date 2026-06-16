package com.gruapim.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "sprint_stories",
       uniqueConstraints = @UniqueConstraint(name = "uq_sprint_story", columnNames = {"sprint_id", "story_id"}),
       indexes = {
           @Index(name = "idx_ss_sprint_id", columnList = "sprint_id"),
           @Index(name = "idx_ss_story_id", columnList = "story_id")
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class SprintStory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sprint_id", nullable = false)
    private Sprint sprint;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "story_id", nullable = false)
    private UserStory story;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private Instant addedAt;
}
