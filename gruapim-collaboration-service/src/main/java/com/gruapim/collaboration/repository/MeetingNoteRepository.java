package com.gruapim.collaboration.repository;

import com.gruapim.collaboration.domain.entity.MeetingNote;
import com.gruapim.collaboration.domain.enums.MeetingType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MeetingNoteRepository extends JpaRepository<MeetingNote, UUID> {

    List<MeetingNote> findBySprintIdOrderByCreatedAtDesc(UUID sprintId);

    List<MeetingNote> findBySprintIdAndType(UUID sprintId, MeetingType type);

    List<MeetingNote> findByProjectIdOrderByCreatedAtDesc(UUID projectId);
}
