package com.gruapim.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "story_comments",
       indexes = @Index(name = "idx_sc_story_id", columnList = "story_id"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoryComment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "story_id", nullable = false)
    private UserStory story;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
}
