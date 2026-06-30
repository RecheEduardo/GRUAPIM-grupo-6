package com.gruapim.event;

import java.util.UUID;

/**
 * Evento de domínio publicado quando uma tarefa é atribuída a um responsável.
 * Carrega tudo que o microsserviço de Colaboração precisa para gerar a
 * notificação sem consultar o monólito (desnormalização no contrato).
 */
public record TaskAssignedEvent(
        UUID taskId,
        String taskTitle,
        UUID assigneeId,
        String assignedByName
) {
    /** Routing key usada no exchange {@code gruapim.events}. */
    public static final String ROUTING_KEY = "task.assigned";
}
