package com.TaskTracker.controller;

import com.TaskTracker.entity.Project;
import com.TaskTracker.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    @Autowired
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping("/create")
    public ResponseEntity<Project> createProject(@RequestParam String name, Authentication authentication) {
        Long ownerId = Long.valueOf(authentication.getName());
        Project project = projectService.createProject(name, ownerId);
        return ResponseEntity.ok(project);
    }

    @GetMapping("/my")
    public ResponseEntity<List<Project>> getUserProjects(Authentication authentication) {
        Long ownerId = Long.valueOf(authentication.getName());
        List<Project> projects = projectService.getUserProjects(ownerId);
        return ResponseEntity.ok(projects);
    }

    @DeleteMapping("/delete/{projectId}")
    public ResponseEntity<String> deleteProject(@PathVariable Long projectId, Authentication authentication) {
        Long ownerId = Long.valueOf(authentication.getName());
        projectService.deleteProject(projectId, ownerId);
        return ResponseEntity.ok("Project deleted successfully");
    }
}
