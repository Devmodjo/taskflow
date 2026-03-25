package io.mvtech.taskflow.task.domain.entity;


import io.mvtech.taskflow.task.domain.entity.enums.TaskStatus;

import java.time.Instant;
import java.util.UUID;

/**
 * Entité Metier de Definit d'un tache
 * pas de @Entity, pas de spring du Java Pur
 */
public class Task {

    private final UUID id;
    private final String taskname;
    private final String description;
    private final String assignedTo;
    private TaskStatus status;      // PENDING, IN_PROGRESS, COMPLETED
    private final Instant createdAt;
    private Instant completedAt;

    /**
     * Constructeur  privéé, on passe par une methode factory
     * pour cree une tache valide
     */
    private Task(UUID id, String taskname, String description, String assignedTo, TaskStatus status ,Instant createdAt) {
        this.id = id;
        this.taskname = taskname;
        this.description = description;
        this.assignedTo = assignedTo;
        this.status = status;
        this.createdAt = createdAt;
    }

    /**
     * Factory methode - elle definit la facon de cree un tache valide
     * sans passé par un constructeur public
     */
    public static Task create(String title, String description, String assignedTo) {
        if (title == null || title.isBlank())
            throw new IllegalArgumentException("Le titre est obligatoire");
        return new Task(UUID.randomUUID(), title, description, assignedTo, TaskStatus.PENDING ,Instant.now());
    }


    public static Task reconstruct(UUID id, String taskname,String description, String assignedTo,
                                  TaskStatus status ,Instant createdAt) {
        return new Task(id, taskname, description, assignedTo, status,
                createdAt);
    }

    // Methode metier : complete() applique les regles de transition d'etat
    public void complete() {
        if (this.status == TaskStatus.COMPLETED)
            throw new IllegalStateException("La tache est deja completee");

        this.status = TaskStatus.COMPLETED;
        this.completedAt = Instant.now();
    }

    /**
     * JUste des Getters pas de Setters car on passe par les methodes metiers
     * @return
     */
    public UUID getId() {
        return id;
    }

    public String getTaskname() {
        return taskname;
    }

    public String getDescription() {
        return description;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }
}
