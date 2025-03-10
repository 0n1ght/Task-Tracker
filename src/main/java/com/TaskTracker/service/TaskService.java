package com.TaskTracker.service;

import com.TaskTracker.entity.Task;
import com.TaskTracker.enums.TaskStatus;

import java.util.List;

public interface TaskService {
    Task createTask(String title, String description, TaskStatus status, Long projectId);
    List<Task> getTasksByProject(Long projectId);
    Task updateTask(Long taskId, String title, String description, TaskStatus status);
    void deleteTask(Long taskId);
}
