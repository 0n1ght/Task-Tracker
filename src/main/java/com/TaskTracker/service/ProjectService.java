package com.TaskTracker.service;

import com.TaskTracker.entity.Project;

import java.util.List;

public interface ProjectService {
    Project createProject(String name, Long ownerId);
    List<Project> getUserProjects(Long ownerId);
    void deleteProject(Long projectId, Long ownerId);
}
