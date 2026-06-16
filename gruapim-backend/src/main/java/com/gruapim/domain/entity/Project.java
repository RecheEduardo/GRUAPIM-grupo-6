package com.gruapim.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "projects",
       indexes = @Index(name = "idx_projects_created_by", columnList = "created_by_id"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Project extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", nullable = false)
    private User createdBy;
}
