package io.mvtech.taskflow.task.domain.event;

import java.time.Instant;
import java.util.UUID;

public record TaskCompletedEvent(
        UUID taskId,
        String assignedTo,
        Instant occuredAt
) {

    /**
     * Constructeur de convenance : occurredAt est fixe au moment de la publication.
     * Appele dans CompleteTaskUseCase : new TaskCompletedEvent(id, assignedTo)
     */
    public TaskCompletedEvent (UUID taskId, String assignedTo){
        this(taskId, assignedTo, Instant.now());
    }


}
