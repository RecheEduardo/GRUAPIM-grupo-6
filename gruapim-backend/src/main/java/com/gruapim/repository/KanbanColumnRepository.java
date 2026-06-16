package com.gruapim.repository;

import com.gruapim.domain.entity.KanbanColumn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface KanbanColumnRepository extends JpaRepository<KanbanColumn, UUID> {

    List<KanbanColumn> findByProjectIdOrderByPositionAsc(UUID projectId);

    Optional<KanbanColumn> findByProjectIdAndTerminalTrue(UUID projectId);

    boolean existsByProjectIdAndName(UUID projectId, String name);

    /** Verifica se há tarefas na coluna antes de permitir remoção (HU14). */
    @Query("SELECT COUNT(t) FROM Task t WHERE t.kanbanColumn.id = :columnId")
    long countTasksByColumnId(@Param("columnId") UUID columnId);

    @Modifying
    @Query("UPDATE KanbanColumn kc SET kc.position = kc.position - 1 WHERE kc.project.id = :projectId AND kc.position > :position")
    void decrementPositionsAfter(@Param("projectId") UUID projectId, @Param("position") int position);

    @Query("SELECT COALESCE(MAX(kc.position), 0) FROM KanbanColumn kc WHERE kc.project.id = :projectId")
    int findMaxPositionByProjectId(@Param("projectId") UUID projectId);

    List<KanbanColumn> findByProjectIdOrderByPosition(UUID projectId);
}
