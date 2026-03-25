package io.mvtech.taskflow.task.domain.exception;

public class TaskNotFoundException extends RuntimeException {
    public TaskNotFoundException() {
        super("Cette tache n'existe pas !");
    }
}
