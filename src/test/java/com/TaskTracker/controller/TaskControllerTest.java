package com.TaskTracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.TaskTracker.entity.Task;
import com.TaskTracker.enums.TaskStatus;
import com.TaskTracker.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    private Task task;

    @BeforeEach
    public void setUp() {
        task = new Task("Task 1", "Task description", TaskStatus.TODO, null);
    }

    @Test
    public void createTask_ShouldReturnTask() throws Exception {
        // Given
        when(taskService.createTask(any(String.class), any(String.class), any(TaskStatus.class), any(Long.class)))
                .thenReturn(task);

        // When & Then
        mockMvc.perform(post("/api/tasks/create")
                        .param("title", "Task 1")
                        .param("description", "Task description")
                        .param("status", "TODO")
                        .param("projectId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Task 1"))
                .andExpect(jsonPath("$.description").value("Task description"))
                .andExpect(jsonPath("$.status").value("TODO"));
    }

    @Test
    public void getTasksByProject_ShouldReturnTasks() throws Exception {
        // Given
        when(taskService.getTasksByProject(any(Long.class)))
                .thenReturn(Collections.singletonList(task));

        // When & Then
        mockMvc.perform(get("/api/tasks/project/{projectId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Task 1"))
                .andExpect(jsonPath("$[0].description").value("Task description"))
                .andExpect(jsonPath("$[0].status").value("TODO"));
    }

    @Test
    public void updateTask_ShouldReturnUpdatedTask() throws Exception {
        // Given
        Task updatedTask = new Task("Updated Task", "Updated description", TaskStatus.DONE, null);
        updatedTask.setId(1L);
        when(taskService.updateTask(any(Long.class), any(String.class), any(String.class), any(TaskStatus.class)))
                .thenReturn(updatedTask);

        // When & Then
        mockMvc.perform(put("/api/tasks/update/{taskId}", 1L)
                        .param("title", "Updated Task")
                        .param("description", "Updated description")
                        .param("status", "DONE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Task"))
                .andExpect(jsonPath("$.description").value("Updated description"))
                .andExpect(jsonPath("$.status").value("DONE"));
    }

    @Test
    public void deleteTask_ShouldReturnNoContent() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/tasks/delete/{taskId}", 1L))
                .andExpect(status().isOk())  // Oczekujemy statusu 200 OK
                .andExpect(content().string("Task deleted successfully"));  // Oczekujemy odpowiedzi tekstowej
    }
}
