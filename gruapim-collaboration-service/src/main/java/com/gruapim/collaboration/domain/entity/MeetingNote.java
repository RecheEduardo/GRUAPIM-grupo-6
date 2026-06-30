package com.gruapim.collaboration.domain.entity;

import com.gruapim.collaboration.domain.enums.MeetingType;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "meeting_notes", indexes = {
    @Index(name = "idx_mn_sprint_id",  columnList = "sprint_id"),
    @Index(name = "idx_mn_project_id", columnList = "project_id")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MeetingNote extends BaseEntity {

    /** Desnormalizado: referência lógica à sprint, sem FK cruzando fronteira. */
    @Column(name = "sprint_id", nullable = false)
    private UUID sprintId;

    /** Armazenado diretamente para suportar consulta "atas do projeto" sem join. */
    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private MeetingType type;

    @Column(columnDefinition = "TEXT")
    private String discussedPoints;

    @Column(columnDefinition = "TEXT")
    private String decisions;

    @Column(columnDefinition = "TEXT")
    private String improvementActions;

    /** Desnormalizado: id e nome do criador vindos das claims JWT. */
    @Column(name = "created_by_id", nullable = false)
    private UUID createdById;

    @Column(name = "created_by_name", nullable = false)
    private String createdByName;
}
