package com.gruapim.collaboration.event;

import com.gruapim.collaboration.domain.enums.NotificationType;
import com.gruapim.collaboration.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.verify;

/**
 * Verifica que um evento task.assigned recebido do broker é mapeado para a
 * notificação correta. O fluxo evento→notificação é o coração da integração
 * assíncrona do microsserviço.
 */
@ExtendWith(MockitoExtension.class)
class NotificationEventListenerTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NotificationEventListener listener;

    @Test
    void deveGerarNotificacaoAoReceberTaskAssigned() {
        UUID taskId = UUID.randomUUID();
        UUID assigneeId = UUID.randomUUID();
        TaskAssignedEvent event = new TaskAssignedEvent(taskId, "Implementar login", assigneeId, "Maria");

        listener.onTaskAssigned(event);

        verify(notificationService).send(
                assigneeId,
                NotificationType.TASK_ASSIGNED,
                "Nova tarefa atribuída",
                "Maria atribuiu a você a tarefa: Implementar login",
                "TASK",
                taskId);
    }
}
