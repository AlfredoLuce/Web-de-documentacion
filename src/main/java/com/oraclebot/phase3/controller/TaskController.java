package com.oraclebot.phase3.controller;

import com.oraclebot.phase3.dto.CreateTaskRequest;
import com.oraclebot.phase3.model.TaskItem;
import com.oraclebot.phase3.service.ProjectWorkspaceService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final ProjectWorkspaceService workspaceService;

    public TaskController(ProjectWorkspaceService workspaceService) {
        this.workspaceService = workspaceService;
    }

    @GetMapping
    public List<TaskItem> getTasks() {
        return workspaceService.findAllTasks();
    }

    @PostMapping
    public TaskItem createTask(@RequestBody CreateTaskRequest request) {
        return workspaceService.createTask(
            request.getTitle(),
            request.getAssignee(),
            request.getStoryPoints() == null ? 3 : request.getStoryPoints(),
            request.getSprintName()
        );
    }
}

