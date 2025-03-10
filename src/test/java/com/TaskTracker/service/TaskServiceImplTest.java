package com.TaskTracker.service;

import com.TaskTracker.entity.Project;
import com.TaskTracker.entity.Task;
import com.TaskTracker.enums.TaskStatus;
import com.TaskTracker.repo.ProjectRepo;
import com.TaskTracker.repo.TaskRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class TaskServiceImplTest {

    @MockBean
    private TaskRepo taskRepo;

    @MockBean
    private ProjectRepo projectRepo;

    @Autowired
    private TaskService taskService;

    private Project project;

    @BeforeEach
    public void setUp() {
        project = new Project("Project 1", null);
    }

    @Test
    public void createTask_ShouldReturnTask() {
        // Given
        Task task = new Task("Task 1", "Task description", TaskStatus.TODO, project);
        when(projectRepo.findById(anyLong())).thenReturn(Optional.of(project));
        when(taskRepo.save(any(Task.class))).thenReturn(task);

        // When
        Task createdTask = taskService.createTask("Task 1", "Task description", TaskStatus.TODO, 1L);

        // Then
        assertNotNull(createdTask);
        assertEquals("Task 1", createdTask.getTitle());
        assertEquals("Task description", createdTask.getDescription());
        assertEquals(TaskStatus.TODO, createdTask.getStatus());
        verify(taskRepo, times(1)).save(any(Task.class));
    }

    @Test
    public void updateTask_ShouldReturnUpdatedTask() {
        // Given
        Task existingTask = new Task("Task 1", "Task description", TaskStatus.TODO, project);
        existingTask.setId(1L);
        when(taskRepo.findById(1L)).thenReturn(Optional.of(existingTask));
        when(taskRepo.save(any(Task.class))).thenReturn(existingTask);

        // When
        Task updatedTask = taskService.updateTask(1L, "Updated Task", "Updated description", TaskStatus.DONE);

        // Then
        assertNotNull(updatedTask);
        assertEquals("Updated Task", updatedTask.getTitle());
        assertEquals("Updated description", updatedTask.getDescription());
        assertEquals(TaskStatus.DONE, updatedTask.getStatus()); // Używamy TaskStatus.DONE
    }

    @Test
    public void deleteTask_ShouldDeleteTask() {
        // Given
        Task existingTask = new Task("Task 1", "Task description", TaskStatus.TODO, project);
        existingTask.setId(1L);
        when(taskRepo.findById(1L)).thenReturn(Optional.of(existingTask));

        // When
        taskService.deleteTask(1L);

        // Then
        verify(taskRepo, times(1)).delete(any(Task.class));
    }
}
