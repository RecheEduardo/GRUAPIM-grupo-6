package com.gruapim.domain.entity;

import com.gruapim.domain.enums.Priority;
import com.gruapim.domain.enums.StoryStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_stories",
       indexes = {
           @Index(name = "idx_us_project_id", columnList = "project_id"),
           @Index(name = "idx_us_status", columnList = "status"),
           @Index(name = "idx_us_priority", columnList = "priority")
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserStory extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private Priority priority = Priority.MEDIUM;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private StoryStatus status = StoryStatus.BACKLOG;

    private Integer storyPoints;

    @Column(name = "position_order", nullable = false)
    @Builder.Default
    private Integer position = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", nullable = false)
    private User createdBy;
}
