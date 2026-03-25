package io.mvtech.taskflow.task.domain.usecase;

import io.mvtech.taskflow.task.domain.entity.Task;
import io.mvtech.taskflow.task.domain.port.TaskRepositoryPort;
import io.mvtech.taskflow.task.domain.usecase.input.CreateTaskCommand;
import io.mvtech.taskflow.task.domain.usecase.output.CreateTaskResult;
import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationEventPublisher;

/**
 * Un UseCase représente exactement une opération métier.
 * Il reçoit une Command (les données d'entrée), orchestre les entités et les Ports, puis retourne un Result.
 * Une seule méthode execute(). Pas d'annotation Spring dans le Domain.
 */

public class CreateTaskUseCase {

    /** On injecte le PORT (interface), pas l'Adapter (implementation concrete).
     * Le UseCase ne sait pas que c'est JPA derriere.
     */
    private final TaskRepositoryPort taskRepositoryPort;

    /** ApplicationEventPublisher : interface Spring, injectee via le constructeur.
     * Permet de publier un Domain Event sans connaitre les modules ecouteurs.
     */
    private final ApplicationEventPublisher event;

    public CreateTaskUseCase(TaskRepositoryPort taskRepositoryPort, ApplicationEventPublisher event) {
        this.taskRepositoryPort = taskRepositoryPort;
        this.event = event;
    }


    @Transactional
    public CreateTaskResult execute(CreateTaskCommand createTaskCommand) {
        Task task = Task.create(
                createTaskCommand.taskname(),
                createTaskCommand.description(),
                createTaskCommand.assignedTo()
        );

        taskRepositoryPort.save(task);
        return new CreateTaskResult(task.getId(), task.getTaskname(), task.getStatus());
    }


}
