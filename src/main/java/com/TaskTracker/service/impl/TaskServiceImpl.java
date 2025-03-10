package com.TaskTracker.service.impl;

import com.TaskTracker.entity.Project;
import com.TaskTracker.entity.Task;
import com.TaskTracker.enums.TaskStatus;
import com.TaskTracker.repo.ProjectRepo;
import com.TaskTracker.repo.TaskRepo;
import com.TaskTracker.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepo taskRepo;
    private final ProjectRepo projectRepo;

    @Autowired
    public TaskServiceImpl(TaskRepo taskRepo, ProjectRepo projectRepo) {
        this.taskRepo = taskRepo;
        this.projectRepo = projectRepo;
    }

    public Task createTask(String title, String description, TaskStatus status, Long projectId) {
        Project project = projectRepo.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        Task task = new Task(title, description, status, project);
        return taskRepo.save(task);
    }

    public List<Task> getTasksByProject(Long projectId) {
        return taskRepo.findByProjectId(projectId);
    }

    public Task updateTask(Long taskId, String title, String description, TaskStatus status) {
        Task task = taskRepo.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        task.setTitle(title);
        task.setDescription(description);
        task.setStatus(status);
        return taskRepo.save(task);
    }

    public void deleteTask(Long taskId) {
        Task task = taskRepo.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        taskRepo.delete(task);
    }
}
