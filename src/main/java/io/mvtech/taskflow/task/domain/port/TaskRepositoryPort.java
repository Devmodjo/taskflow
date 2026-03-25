package io.mvtech.taskflow.task.domain.port;

import io.mvtech.taskflow.task.domain.entity.Task;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Le port est une interface Java qui Exprime ce que notre
 * UseCase a Besoin sans dire comment c'est fait.
 * NB: A titre de Rappel, nous somme dans la couche domain celle ci n'as pas
 * connaissance du framework juste des classes Java Pur.
 */

public interface TaskRepositoryPort {

    void save (Task task);

    Optional<Task> findById(UUID id);

    List<Task> findByAssignedTo(String assignedTo);

}
