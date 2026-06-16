package com.gruapim.repository;

import com.gruapim.domain.entity.UserStory;
import com.gruapim.domain.enums.Priority;
import com.gruapim.domain.enums.StoryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserStoryRepository extends JpaRepository<UserStory, UUID> {

    List<UserStory> findByProjectIdOrderByPositionAsc(UUID projectId);

    List<UserStory> findByProjectIdAndStatusOrderByPositionAsc(UUID projectId, StoryStatus status);

    List<UserStory> findByProjectIdAndPriorityOrderByPositionAsc(UUID projectId, Priority priority);

    List<UserStory> findByProjectIdAndStatusAndPriorityOrderByPositionAsc(UUID projectId, StoryStatus status, Priority priority);

    long countByProjectIdAndStatus(UUID projectId, StoryStatus status);

    @Query("SELECT MAX(us.position) FROM UserStory us WHERE us.project.id = :projectId")
    Integer findMaxPositionByProjectId(@Param("projectId") UUID projectId);

    @Modifying
    @Query("UPDATE UserStory us SET us.position = us.position - 1 WHERE us.project.id = :projectId AND us.position > :position")
    void decrementPositionsAfter(@Param("projectId") UUID projectId, @Param("position") int position);
}
