package com.gruapim.event;

import com.gruapim.config.RabbitConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * Publica eventos de domínio no broker. As falhas de publicação são apenas
 * logadas (degradação graciosa): o core domain — Sprints/Kanban — não pode
 * cair porque o broker está indisponível. A resiliência (retry/circuit breaker)
 * é endurecida no commit 8.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DomainEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishTaskAssigned(TaskAssignedEvent event) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitConfig.EVENTS_EXCHANGE,
                    TaskAssignedEvent.ROUTING_KEY,
                    event);
            log.debug("Evento publicado: {} taskId={}", TaskAssignedEvent.ROUTING_KEY, event.taskId());
        } catch (Exception e) {
            log.warn("Falha ao publicar TaskAssignedEvent (taskId={}): {}", event.taskId(), e.getMessage());
        }
    }
}
