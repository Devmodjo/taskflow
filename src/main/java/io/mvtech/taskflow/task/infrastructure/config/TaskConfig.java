package io.mvtech.taskflow.task.infrastructure.config;

import io.mvtech.taskflow.task.domain.port.TaskRepositoryPort;
import io.mvtech.taskflow.task.domain.usecase.CompleteTaskUseCase;
import io.mvtech.taskflow.task.domain.usecase.CreateTaskUseCase;
import io.mvtech.taskflow.task.domain.usecase.GetTaskUseCase;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TaskConfig {

    /**
     * Spring resout TaskRepositoryPort en injectant TaskRepositoryAdapter
     * car c'est l'unique @Component qui implements TaskRepositoryPort.
     * Spring resout ApplicationEventPublisher automatiquement :
     * c'est un bean interne du contexte Spring, toujours disponible
      */
    @Bean
    public CreateTaskUseCase createTaskUseCase(TaskRepositoryPort taskRepository, ApplicationEventPublisher event) {
        return new CreateTaskUseCase(taskRepository, event);
    }

    @Bean
    public CompleteTaskUseCase completeTaskUseCase(TaskRepositoryPort taskRepository, ApplicationEventPublisher event) {
        return new CompleteTaskUseCase(taskRepository, event);
    }

    @Bean
    public GetTaskUseCase getTaskUseCase(TaskRepositoryPort taskRepository, ApplicationEventPublisher event) {
        return new GetTaskUseCase(taskRepository, event);
    }
}