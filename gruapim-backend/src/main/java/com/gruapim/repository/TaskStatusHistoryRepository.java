package com.gruapim.repository;

import com.gruapim.domain.entity.TaskStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TaskStatusHistoryRepository extends JpaRepository<TaskStatusHistory, UUID> {

    List<TaskStatusHistory> findByTaskIdOrderByChangedAtAsc(UUID taskId);

    List<TaskStatusHistory> findByChangedByIdOrderByChangedAtDesc(UUID userId);
}
