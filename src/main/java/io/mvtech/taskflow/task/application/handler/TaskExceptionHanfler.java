package io.mvtech.taskflow.task.application.handler;


import io.mvtech.taskflow.task.domain.exception.TaskNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class TaskExceptionHanfler {

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<Map<String, Object>> taskNotFoundException(TaskNotFoundException err) {

        Map<String, Object> errMap = new HashMap<>();
        errMap.put("date", LocalDate.now());
        errMap.put("msg", err.getMessage());
        errMap.put("path", err.getCause());

        return ResponseEntity.status(404).body(errMap);
    }

}
