package com.gruapim.repository;

import com.gruapim.domain.entity.StoryComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StoryCommentRepository extends JpaRepository<StoryComment, UUID> {

    /** Comentários em ordem cronológica — exibidos no detalhe da estória (HU11). */
    List<StoryComment> findByStoryIdOrderByCreatedAtAsc(UUID storyId);

    long countByStoryId(UUID storyId);
}
