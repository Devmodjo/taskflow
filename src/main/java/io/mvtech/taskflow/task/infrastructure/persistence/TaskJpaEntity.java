package io.mvtech.taskflow.task.infrastructure.persistence;


import io.mvtech.taskflow.task.domain.entity.Task;
import io.mvtech.taskflow.task.domain.entity.enums.TaskStatus;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "task")
public class TaskJpaEntity {

    @Id
    // @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String taskname;
    private String description;
    private String assignedTo;
    @Enumerated(EnumType.STRING)
    private TaskStatus status;
    private Instant createdAt;
    private Instant completedAt;

    // Mapping du Infrastructure vers Domain
    public Task toDomain() {
        return Task.reconstruct(id, taskname, description, assignedTo, status,createdAt);
    }

    // Mapping Domain vers l'infra
    public static TaskJpaEntity from(Task task) {
        TaskJpaEntity e = new TaskJpaEntity();
        e.id          = task.getId();
        e.taskname       = task.getTaskname();
        e.description = task.getDescription();
        e.assignedTo  = task.getAssignedTo();
        e.status      = task.getStatus();
        e.createdAt   = task.getCreatedAt();
        e.completedAt = task.getCompletedAt();

        return e;
    }
}
