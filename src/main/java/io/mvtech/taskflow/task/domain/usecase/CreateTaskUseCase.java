package io.mvtech.taskflow.task.domain.usecase;

import io.mvtech.taskflow.task.domain.port.TaskRepositoryPort;
import org.springframework.context.ApplicationEventPublisher;

/**
 * Un UseCase représente exactement une opération métier.
 * Il reçoit une Command (les données d'entrée), orchestre les entités et les Ports, puis retourne un Result.
 * Une seule méthode execute(). Pas d'annotation Spring dans le Domain.
 */

public class CreateTaskUseCase {

    private final TaskRepositoryPort taskRepositoryPort;
    private final ApplicationEventPublisher event;

    public CreateTaskUseCase(TaskRepositoryPort taskRepositoryPort, ApplicationEventPublisher event) {
        this.taskRepositoryPort = taskRepositoryPort;
        this.event = event;
    }


}
