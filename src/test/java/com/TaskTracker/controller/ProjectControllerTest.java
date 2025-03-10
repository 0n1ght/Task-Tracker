package com.TaskTracker.controller;

import com.TaskTracker.entity.Project;
import com.TaskTracker.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ProjectControllerTest {

    @InjectMocks
    private ProjectController projectController;

    @Mock
    private ProjectService projectService;

    @Mock
    private Authentication authentication;

    private Long ownerId = 1L;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(authentication.getName()).thenReturn(ownerId.toString());
    }

    @Test
    void createProject_ShouldReturnProject_WhenValidRequestIsMade() {
        // Given
        String projectName = "Test Project";
        Project project = new Project(projectName, null);
        when(projectService.createProject(projectName, ownerId)).thenReturn(project);

        // When
        ResponseEntity<Project> response = projectController.createProject(projectName, authentication);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(projectName, response.getBody().getName());
        verify(projectService, times(1)).createProject(projectName, ownerId);
    }

    @Test
    void getUserProjects_ShouldReturnListOfProjects_WhenUserHasProjects() {
        // Given
        List<Project> projects = Collections.singletonList(new Project("Test Project", null));
        when(projectService.getUserProjects(ownerId)).thenReturn(projects);

        // When
        ResponseEntity<List<Project>> response = projectController.getUserProjects(authentication);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        assertEquals("Test Project", response.getBody().get(0).getName());
        verify(projectService, times(1)).getUserProjects(ownerId);
    }

    @Test
    void getUserProjects_ShouldReturnEmptyList_WhenUserHasNoProjects() {
        // Given
        List<Project> projects = Collections.emptyList();
        when(projectService.getUserProjects(ownerId)).thenReturn(projects);

        // When
        ResponseEntity<List<Project>> response = projectController.getUserProjects(authentication);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isEmpty());
        verify(projectService, times(1)).getUserProjects(ownerId);
    }

    @Test
    void deleteProject_ShouldReturnSuccessMessage_WhenProjectIsDeleted() {
        // Given
        Long projectId = 1L;

        // When
        ResponseEntity<String> response = projectController.deleteProject(projectId, authentication);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Project deleted successfully", response.getBody());
        verify(projectService, times(1)).deleteProject(projectId, ownerId);
    }
}
