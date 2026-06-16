package com.gruapim.repository;

import com.gruapim.domain.entity.SprintStory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SprintStoryRepository extends JpaRepository<SprintStory, UUID> {

    List<SprintStory> findBySprintId(UUID sprintId);

    Optional<SprintStory> findBySprintIdAndStoryId(UUID sprintId, UUID storyId);

    boolean existsBySprintIdAndStoryId(UUID sprintId, UUID storyId);

    @Modifying
    @Query("DELETE FROM SprintStory ss WHERE ss.sprint.id = :sprintId AND ss.story.id = :storyId")
    void deleteBySprintIdAndStoryId(@Param("sprintId") UUID sprintId, @Param("storyId") UUID storyId);

    /** Soma de story points alocados na sprint — exibida no planning (HU04). */
    @Query("""
        SELECT COALESCE(SUM(ss.story.storyPoints), 0)
        FROM SprintStory ss
        WHERE ss.sprint.id = :sprintId
          AND ss.story.storyPoints IS NOT NULL
        """)
    int sumStoryPointsBySprintId(@Param("sprintId") UUID sprintId);
}
