package io.mvtech.taskflow.task.domain.usecase;

import io.mvtech.taskflow.task.domain.entity.Task;
import io.mvtech.taskflow.task.domain.event.TaskCompletedEvent;
import io.mvtech.taskflow.task.domain.port.TaskRepositoryPort;
import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;
import java.util.UUID;

public class CompleteTaskUseCase {

    private final TaskRepositoryPort taskRepositoryPort;
    private final ApplicationEventPublisher events;


    public CompleteTaskUseCase(TaskRepositoryPort taskRepositoryPort, ApplicationEventPublisher events) {
        this.taskRepositoryPort = taskRepositoryPort;
        this.events = events;
    }

    @Transactional
    public void execute(UUID id) {

        Optional<Task> task = Optional.ofNullable(taskRepositoryPort.findById(id)
                .orElseThrow(() -> {
                    throw new IllegalArgumentException("cette tâche n'existe pas");
                }));
        /**
         * on utilise la methode complete cree dans Task pour changer l'etat d'une
         * tache sans toute fois passsé par des setters
         */
        Task getTask = task.get();
        getTask.complete();
        taskRepositoryPort.save(getTask);

        /**. Publier le Domain Event.
         * Spring appelle tous les @EventListener qui acceptent TaskCompletedEvent.
         * Ce UseCase ne sait pas combien il y en a, ni ce qu'ils font.
         */
        events.publishEvent(new TaskCompletedEvent(getTask.getId(), getTask.getAssignedTo()));
    }
}
