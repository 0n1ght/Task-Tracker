package com.TaskTracker.controller;

import com.TaskTracker.entity.Task;
import com.TaskTracker.enums.TaskStatus;
import com.TaskTracker.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/create")
    public ResponseEntity<Task> createTask(
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam TaskStatus status,
            @RequestParam Long projectId) {

        Task task = taskService.createTask(title, description, status, projectId);
        return ResponseEntity.ok(task);
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<Task>> getTasksByProject(@PathVariable Long projectId) {
        List<Task> tasks = taskService.getTasksByProject(projectId);
        return ResponseEntity.ok(tasks);
    }

    @PutMapping("/update/{taskId}")
    public ResponseEntity<Task> updateTask(
            @PathVariable Long taskId,
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam TaskStatus status) {

        Task updatedTask = taskService.updateTask(taskId, title, description, status);
        return ResponseEntity.ok(updatedTask);
    }

    @DeleteMapping("/delete/{taskId}")
    public ResponseEntity<String> deleteTask(@PathVariable Long taskId) {
        taskService.deleteTask(taskId);
        return ResponseEntity.ok("Task deleted successfully");
    }
}
