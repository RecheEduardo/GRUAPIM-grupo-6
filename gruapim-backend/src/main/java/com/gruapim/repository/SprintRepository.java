package com.gruapim.repository;

import com.gruapim.domain.entity.Sprint;
import com.gruapim.domain.enums.SprintStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SprintRepository extends JpaRepository<Sprint, UUID> {

    List<Sprint> findByProjectIdOrderByStartDateAsc(UUID projectId);

    Optional<Sprint> findFirstByProjectIdAndStatus(UUID projectId, SprintStatus status);

    List<Sprint> findByProjectIdAndStatus(UUID projectId, SprintStatus status);

    /** Detecta sobreposição de datas entre sprints não-concluídas (HU03). */
    @Query("""
        SELECT s FROM Sprint s
        WHERE s.project.id = :projectId
          AND s.status != 'COMPLETED'
          AND s.startDate <= :endDate
          AND s.endDate >= :startDate
          AND s.id != :excludeId
        """)
    List<Sprint> findOverlapping(@Param("projectId") UUID projectId,
                                 @Param("startDate") LocalDate startDate,
                                 @Param("endDate") LocalDate endDate,
                                 @Param("excludeId") UUID excludeId);

    /** Sprints ativas que encerram na data informada — usada pelo scheduler de notificações (HU12). */
    @Query("SELECT s FROM Sprint s WHERE s.status = 'IN_PROGRESS' AND s.endDate = :date")
    List<Sprint> findActiveSprintsEndingOn(@Param("date") LocalDate date);

    List<Sprint> findByProjectIdOrderByStartDate(UUID projectId);
    boolean existsByProjectIdAndStatus(UUID projectId, SprintStatus status);
}
