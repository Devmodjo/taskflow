package io.mvtech.taskflow.task.application.dto;

import io.mvtech.taskflow.task.domain.entity.enums.TaskStatus;
import io.mvtech.taskflow.task.domain.usecase.output.CreateTaskResult;

import java.util.UUID;

public record TaskResponse(
        UUID id,
        String title,
        TaskStatus status,
        String assignedTo
) {

    /**
     * Factory method : convertit un CreateTaskResult (Domain) en TaskResponse.
     * Appele dans le Controller apres execution du UseCase.
     * @param result
     * @return
     */
    public static TaskResponse from(CreateTaskResult result) {
        return new TaskResponse(
                result.taskId(),
                result.taskname(),
                result.status(), // TaskStatus enum -> String pour le JSON
                null                    // assignedTo pas dans CreateTaskResult ici
        );
    }
}
