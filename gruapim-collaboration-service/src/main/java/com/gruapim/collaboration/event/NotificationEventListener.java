package com.gruapim.collaboration.event;

import com.gruapim.collaboration.config.RabbitConfig;
import com.gruapim.collaboration.domain.enums.NotificationType;
import com.gruapim.collaboration.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Consome eventos de domínio do monólito e gera notificações.
 * Substitui a antiga chamada síncrona direta a {@code NotificationService}
 * por integração assíncrona (event-driven).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationService notificationService;

    @RabbitListener(queues = RabbitConfig.NOTIFICATIONS_QUEUE)
    public void onTaskAssigned(TaskAssignedEvent event) {
        log.debug("Evento recebido: task.assigned taskId={}", event.taskId());
        notificationService.send(
                event.assigneeId(),
                NotificationType.TASK_ASSIGNED,
                "Nova tarefa atribuída",
                event.assignedByName() + " atribuiu a você a tarefa: " + event.taskTitle(),
                "TASK",
                event.taskId());
    }
}
