package com.gruapim.repository;

import com.gruapim.domain.entity.MeetingNote;
import com.gruapim.domain.enums.MeetingType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MeetingNoteRepository extends JpaRepository<MeetingNote, UUID> {

    List<MeetingNote> findBySprintIdOrderByCreatedAtDesc(UUID sprintId);

    List<MeetingNote> findBySprintIdAndType(UUID sprintId, MeetingType type);

    /** Todas as atas do projeto — histórico de cerimônias (HU09). */
    @Query("""
        SELECT mn FROM MeetingNote mn
        WHERE mn.sprint.project.id = :projectId
        ORDER BY mn.createdAt DESC
        """)
    List<MeetingNote> findByProjectIdOrderByCreatedAtDesc(@Param("projectId") UUID projectId);
}
