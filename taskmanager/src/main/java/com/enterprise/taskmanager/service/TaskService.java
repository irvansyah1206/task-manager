package com.enterprise.taskmanager.service;

import com.enterprise.taskmanager.dto.response.TaskResponse;
import com.enterprise.taskmanager.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    public List<TaskResponse> getTasksByUser(Long userId) {
        return taskRepository.findByUserId(userId).stream()
                .map(task -> TaskResponse.builder()
                        .id(task.getId())
                        .title(task.getTitle())
                        .status(task.getStatus().name())
                        .username(task.getUser().getUsername())
                        .build())
                .collect(Collectors.toList());
    }
}
