package com.gruapim.collaboration.repository;

import com.gruapim.collaboration.domain.entity.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, UUID> {

    List<ChatMessage> findByProjectIdOrderBySentAtAsc(UUID projectId);

    Page<ChatMessage> findByProjectIdOrderBySentAtDesc(UUID projectId, Pageable pageable);
}
