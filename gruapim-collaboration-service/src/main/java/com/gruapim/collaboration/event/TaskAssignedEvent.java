package com.gruapim.collaboration.event;

import java.util.UUID;

/**
 * Cópia do contrato de evento publicado pelo monólito (gruapim-backend).
 * Microsserviços não compartilham código: o contrato é duplicado de forma
 * deliberada para que produtor e consumidor evoluam de forma independente.
 */
public record TaskAssignedEvent(
        UUID taskId,
        String taskTitle,
        UUID assigneeId,
        String assignedByName
) {
}
