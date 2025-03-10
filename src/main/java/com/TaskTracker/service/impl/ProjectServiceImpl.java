package com.TaskTracker.service.impl;

import com.TaskTracker.entity.Project;
import com.TaskTracker.entity.Account;
import com.TaskTracker.repo.ProjectRepo;
import com.TaskTracker.repo.AccountRepo;
import com.TaskTracker.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepo projectRepo;
    private final AccountRepo accountRepo;

    @Autowired
    public ProjectServiceImpl(ProjectRepo projectRepo, AccountRepo accountRepo) {
        this.projectRepo = projectRepo;
        this.accountRepo = accountRepo;
    }

    public Project createProject(String name, Long ownerId) {
        Account owner = accountRepo.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("Owner not found"));

        Project project = new Project(name, owner);
        return projectRepo.save(project);
    }

    public List<Project> getUserProjects(Long ownerId) {
        return projectRepo.findByOwnerId(ownerId);
    }

    public void deleteProject(Long projectId, Long ownerId) {
        Project project = projectRepo.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (!project.getOwner().getId().equals(ownerId)) {
            throw new RuntimeException("Unauthorized to delete this project");
        }

        projectRepo.delete(project);
    }
}
