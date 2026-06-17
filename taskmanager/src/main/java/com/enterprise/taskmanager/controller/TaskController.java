package com.enterprise.taskmanager.controller;

import com.enterprise.taskmanager.dto.request.TaskRequest;
import com.enterprise.taskmanager.dto.response.AuditLogDashboardResponse;
import com.enterprise.taskmanager.dto.response.TaskResponse;
import com.enterprise.taskmanager.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TaskResponse>> getTasksByUser (@PathVariable Long userId) {
        return ResponseEntity.ok(taskService.getTasksByUser(userId));
    }

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@RequestBody TaskRequest request) {
        return ResponseEntity.ok(taskService.createTask(request));
    }

    @GetMapping("/analytics/dashboard")
    public ResponseEntity<AuditLogDashboardResponse> getDashboardAnalytics() {
        return ResponseEntity.ok(taskService.getElasticsearchAnalytics());
    }
}

