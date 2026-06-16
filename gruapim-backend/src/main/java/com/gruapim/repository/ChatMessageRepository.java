package com.gruapim.repository;

import com.gruapim.domain.entity.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, UUID> {

    /** Histórico completo — carregado ao abrir o chat (HU10). */
    List<ChatMessage> findByProjectIdOrderBySentAtAsc(UUID projectId);

    /** Histórico paginado — para projetos com muitas mensagens. */
    Page<ChatMessage> findByProjectIdOrderBySentAtDesc(UUID projectId, Pageable pageable);
}
