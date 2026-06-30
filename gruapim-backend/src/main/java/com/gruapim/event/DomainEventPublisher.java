package com.gruapim.event;

import com.gruapim.config.RabbitConfig;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * Publica eventos de domínio no broker com resiliência: até N tentativas
 * (retry) e um circuit breaker que evita martelar um broker indisponível.
 * Esgotadas as tentativas, o fallback apenas loga — o core domain
 * (Sprints/Kanban) nunca cai por causa da mensageria (degradação graciosa).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DomainEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Retry(name = "eventPublisher")
    @CircuitBreaker(name = "eventPublisher", fallbackMethod = "publishFallback")
    public void publishTaskAssigned(TaskAssignedEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitConfig.EVENTS_EXCHANGE,
                TaskAssignedEvent.ROUTING_KEY,
                event);
        log.debug("Evento publicado: {} taskId={}", TaskAssignedEvent.ROUTING_KEY, event.taskId());
    }

    @SuppressWarnings("unused")
    private void publishFallback(TaskAssignedEvent event, Throwable t) {
        log.warn("Falha ao publicar TaskAssignedEvent (taskId={}) após retries: {}",
                event.taskId(), t.getMessage());
    }
}
