package io.mvtech.taskflow.task.domain.usecase.output;

import io.mvtech.taskflow.task.domain.entity.enums.TaskStatus;

import java.util.UUID;

public record CreateTaskResult(
        UUID taskId,
        String taskname,
        TaskStatus status
) {
}
