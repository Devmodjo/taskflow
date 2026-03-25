package io.mvtech.taskflow.task.infrastructure.persistence;

import io.mvtech.taskflow.task.domain.entity.Task;
import io.mvtech.taskflow.task.domain.port.TaskRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * L'adapter est cette classe la qui implemente les different methode du port
 */
@Component
@RequiredArgsConstructor
public class TaskRepositoryAdapter implements TaskRepositoryPort {

    private final TaskJpaRepository jpa;

    @Override
    public void save(Task task) {
        jpa.save(TaskJpaEntity.from(task));
    }

    @Override
    public Optional<Task> findById(UUID id) {
        return jpa.findById(id).map(TaskJpaEntity::toDomain);
    }

    @Override
    public List<Task> findByAssignedTo(String assignedTo) {
        return jpa.findByAssignedTo(assignedTo)
                .stream()
                .map(TaskJpaEntity::toDomain)
                .toList();
    }

    @Override
    public List<Task> findAll() {
        ArrayList<Task> list = new ArrayList<>();
        jpa.findAll().forEach(t -> list.add(t.toDomain()));
        return list;
    }
}
