package io.mvtech.taskflow.task.domain.usecase.input;

public record CreateTaskCommand(
        String taskname,
        String description,
        String assignedTo
) {
}
