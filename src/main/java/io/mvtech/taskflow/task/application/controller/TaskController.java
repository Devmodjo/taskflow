package io.mvtech.taskflow.task.application.controller;


import io.mvtech.taskflow.task.application.dto.CreateTaskRequest;
import io.mvtech.taskflow.task.application.dto.TaskResponse;
import io.mvtech.taskflow.task.domain.usecase.CompleteTaskUseCase;
import io.mvtech.taskflow.task.domain.usecase.CreateTaskUseCase;
import io.mvtech.taskflow.task.domain.usecase.GetTaskUseCase;
import io.mvtech.taskflow.task.domain.usecase.input.CreateTaskCommand;
import io.mvtech.taskflow.task.domain.usecase.output.CreateTaskResult;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/task")
public class TaskController {

    /**
     * ici, au lieur d'injecté un seul service qui contient toute nos methodes,
     * on injecte nos different usecase
     */
    private final CreateTaskUseCase createTask;
    private final CompleteTaskUseCase completeTask;
    private final GetTaskUseCase getTask;


    public TaskController(CreateTaskUseCase createTask, CompleteTaskUseCase completeTask, GetTaskUseCase getTask) {
        this.createTask = createTask;
        this.completeTask = completeTask;
        this.getTask = getTask;
    }

    @Operation(summary = "api de creation de nouvelle tâche")
    @PostMapping("/create")
    public ResponseEntity<TaskResponse> createTaskUseCase(@RequestBody CreateTaskRequest taskRequest) {

        CreateTaskCommand command = new CreateTaskCommand(taskRequest.taskname(), taskRequest.description(), taskRequest.assignedTo());
        CreateTaskResult taskResult = createTask.execute(command);

        return ResponseEntity.status(201).body(TaskResponse.from(taskResult));

    }

    @Operation(summary = "affichage des tache enregistrer")
    @GetMapping
    public ResponseEntity<List<TaskResponse>> getTaskUseCase () {

        List<CreateTaskResult> results = getTask.execute();
        return ResponseEntity.status(200).body(results.stream().map(TaskResponse::from).toList());
    }

}
