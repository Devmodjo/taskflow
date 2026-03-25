package io.mvtech.taskflow.task.domain.usecase;

import io.mvtech.taskflow.task.domain.port.TaskRepositoryPort;
import io.mvtech.taskflow.task.domain.usecase.output.CreateTaskResult;
import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationEventPublisher;

import java.util.ArrayList;
import java.util.List;

public class GetTaskUseCase {

    private final TaskRepositoryPort taskRepositoryPort;
    private final ApplicationEventPublisher event;

    public GetTaskUseCase(TaskRepositoryPort taskRepositoryPort, ApplicationEventPublisher event) {
        this.taskRepositoryPort = taskRepositoryPort;
        this.event = event;
    }

    @Transactional
    public List<CreateTaskResult> execute() {
        ArrayList<CreateTaskResult> list = new ArrayList<>();
        taskRepositoryPort.findAll().forEach(t-> list.add(
                new CreateTaskResult(t.getId(), t.getTaskname(), t.getStatus())
        ));
        return list;
    }
}
