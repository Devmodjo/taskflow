package io.mvtech.taskflow.task.application.dto;

public record CreateTaskRequest(
        String taskname,
        String description,
        String assignedTo
) {
}
