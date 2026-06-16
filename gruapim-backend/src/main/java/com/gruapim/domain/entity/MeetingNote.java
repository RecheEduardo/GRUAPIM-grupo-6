package com.gruapim.domain.entity;

import com.gruapim.domain.enums.MeetingType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "meeting_notes",
       indexes = @Index(name = "idx_mn_sprint_id", columnList = "sprint_id"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeetingNote extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sprint_id", nullable = false)
    private Sprint sprint;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private MeetingType type;

    @Column(columnDefinition = "TEXT")
    private String discussedPoints;

    @Column(columnDefinition = "TEXT")
    private String decisions;

    @Column(columnDefinition = "TEXT")
    private String improvementActions;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", nullable = false)
    private User createdBy;
}
