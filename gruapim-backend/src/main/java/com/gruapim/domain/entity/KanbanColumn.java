package com.gruapim.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "kanban_columns",
       indexes = @Index(name = "idx_kc_project_id", columnList = "project_id"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KanbanColumn extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private Integer position;

    /**
     * Coluna terminal indica "concluído" para cálculo do burndown.
     * Não pode ser removida enquanto contiver tarefas.
     */
    @Column(nullable = false)
    @Builder.Default
    private boolean terminal = false;
}
