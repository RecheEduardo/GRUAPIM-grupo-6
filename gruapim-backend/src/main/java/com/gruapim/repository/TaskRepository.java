package com.gruapim.repository;

import com.gruapim.domain.entity.Task;
import com.gruapim.domain.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {

    List<Task> findByStoryId(UUID storyId);

    List<Task> findByStoryIdAndStatus(UUID storyId, TaskStatus status);

    List<Task> findByAssigneeId(UUID assigneeId);

    List<Task> findByKanbanColumnId(UUID columnId);

    long countByStoryId(UUID storyId);

    long countByStoryIdAndStatus(UUID storyId, TaskStatus status);

    /** Todas as tarefas de uma sprint — usadas no Kanban (HU06) e Burndown (HU05). */
    @Query("""
        SELECT t FROM Task t
        JOIN SprintStory ss ON ss.story = t.story
        WHERE ss.sprint.id = :sprintId
        """)
    List<Task> findBySprintId(@Param("sprintId") UUID sprintId);

    /** Tarefas não-concluídas de uma sprint — linha real do burndown (HU05). */
    @Query("""
        SELECT t FROM Task t
        JOIN SprintStory ss ON ss.story = t.story
        WHERE ss.sprint.id = :sprintId
          AND t.status != 'DONE'
        """)
    List<Task> findPendingBySprintId(@Param("sprintId") UUID sprintId);

    /** Tarefas de uma sprint agrupadas por coluna Kanban — para montagem do quadro. */
    @Query("""
        SELECT t FROM Task t
        JOIN SprintStory ss ON ss.story = t.story
        WHERE ss.sprint.id = :sprintId
        ORDER BY t.kanbanColumn.position ASC NULLS LAST
        """)
    List<Task> findBySprintIdOrderByColumn(@Param("sprintId") UUID sprintId);
}
