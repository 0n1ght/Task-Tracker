package com.TaskTracker.service;

import com.TaskTracker.entity.Project;
import com.TaskTracker.entity.Account;
import com.TaskTracker.repo.ProjectRepo;
import com.TaskTracker.repo.AccountRepo;
import com.TaskTracker.service.impl.ProjectServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ProjectServiceImplTest {

    @InjectMocks
    private ProjectServiceImpl projectService;

    @Mock
    private ProjectRepo projectRepo;

    @Mock
    private AccountRepo accountRepo;

    private Account owner;
    private Project project;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        owner = new Account();
        owner.setId(1L);
        project = new Project("Test Project", owner);
    }

    @Test
    void createProject_ShouldCreateProject_WhenOwnerExists() {
        // Given
        Long ownerId = 1L;
        String projectName = "Test Project";
        when(accountRepo.findById(ownerId)).thenReturn(Optional.of(owner));
        when(projectRepo.save(any(Project.class))).thenReturn(project);

        // When
        Project createdProject = projectService.createProject(projectName, ownerId);

        // Then
        assertNotNull(createdProject);
        assertEquals(projectName, createdProject.getName());
        assertEquals(ownerId, createdProject.getOwner().getId());
        verify(accountRepo, times(1)).findById(ownerId);
        verify(projectRepo, times(1)).save(any(Project.class));
    }

    @Test
    void createProject_ShouldThrowException_WhenOwnerNotFound() {
        // Given
        Long ownerId = 1L;
        String projectName = "Test Project";
        when(accountRepo.findById(ownerId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> projectService.createProject(projectName, ownerId));
        verify(accountRepo, times(1)).findById(ownerId);
        verify(projectRepo, never()).save(any(Project.class));
    }

    @Test
    void getUserProjects_ShouldReturnListOfProjects_WhenProjectsExistForUser() {
        // Given
        Long ownerId = 1L;
        when(projectRepo.findByOwnerId(ownerId)).thenReturn(List.of(project));

        // When
        List<Project> projects = projectService.getUserProjects(ownerId);

        // Then
        assertNotNull(projects);
        assertEquals(1, projects.size());
        assertEquals("Test Project", projects.get(0).getName());
        verify(projectRepo, times(1)).findByOwnerId(ownerId);
    }

    @Test
    void deleteProject_ShouldThrowException_WhenUnauthorizedOwner() {
        // Given
        Long projectId = 1L;
        Long ownerId = 1L;
        Long unauthorizedOwnerId = 2L;
        when(projectRepo.findById(projectId)).thenReturn(Optional.of(project));

        // When & Then
        assertThrows(RuntimeException.class, () -> projectService.deleteProject(projectId, unauthorizedOwnerId));
        verify(projectRepo, never()).delete(any(Project.class));
    }

    @Test
    void deleteProject_ShouldThrowException_WhenProjectNotFound() {
        // Given
        Long projectId = 1L;
        Long ownerId = 1L;
        when(projectRepo.findById(projectId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> projectService.deleteProject(projectId, ownerId));
        verify(projectRepo, never()).delete(any(Project.class));
    }
}
