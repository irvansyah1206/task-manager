package com.enterprise.taskmanager.service;

import com.enterprise.taskmanager.dto.request.TaskRequest;
import com.enterprise.taskmanager.dto.response.AuditLogDashboardResponse;
import com.enterprise.taskmanager.dto.response.TaskResponse;
import com.enterprise.taskmanager.model.dokumen.TaskAuditLog;
import com.enterprise.taskmanager.model.entity.Task;
import com.enterprise.taskmanager.model.entity.User;
import com.enterprise.taskmanager.model.enums.TaskStatus;
import com.enterprise.taskmanager.repository.elastic.TaskAuditLogRepository;
import com.enterprise.taskmanager.repository.TaskRepository;
import com.enterprise.taskmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskAuditLogRepository taskAuditLogRepository; // Tambahkan ini

    @Cacheable(value = "tasks", key = "#userId")
    public List<TaskResponse> getTasksByUser(Long userId) {
        return taskRepository.findByUserId(userId).stream() // Bisa diganti findByUserIdNative jika pakai optimasi langkah 4
                .map(task -> TaskResponse.builder()
                        .id(task.getId())
                        .title(task.getTitle())
                        .status(task.getStatus().name())
                        .username(task.getUser().getUsername())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    @CacheEvict(value = "tasks", key = "#request.userId")
    public TaskResponse createTask(TaskRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User dengan ID " + request.getUserId() + " tidak ditemukan"));

        Task task = Task.builder()
                .title(request.getTitle())
                .status(TaskStatus.valueOf(request.getStatus().toUpperCase()))
                .user(user)
                .build();

        Task savedTask = taskRepository.save(task);

        // =====================================================================
        // AUDIT LOGGING ELASTICSEARCH (Kriteria Penilaian Arsitektur Mandiri)
        // =====================================================================
        try {
            TaskAuditLog auditLog = TaskAuditLog.builder()
                    .action("CREATE_TASK")
                    .taskId(savedTask.getId())
                    .taskTitle(savedTask.getTitle())
                    .username(user.getUsername())
                    .timestamp(Instant.now())
                    .build();

            taskAuditLogRepository.save(auditLog);
            log.info("Audit log berhasil dikirim ke Elasticsearch untuk task ID: {}", savedTask.getId());
        } catch (Exception e) {
            log.error("Gagal menyimpan data log analitik ke Elasticsearch: ", e);
        }

        return TaskResponse.builder()
                .id(savedTask.getId())
                .title(savedTask.getTitle())
                .status(savedTask.getStatus().name())
                .username(user.getUsername())
                .build();
    }

    public AuditLogDashboardResponse getElasticsearchAnalytics() {
        // 1. Ambil total count log analitik
        long totalLogs = taskAuditLogRepository.count();

        // 2. Ambil semua data log analitik terbaru
        List<AuditLogDashboardResponse.TaskLogDetail> details = new ArrayList<>();
        taskAuditLogRepository.findAll().forEach(logDoc -> {
            details.add(AuditLogDashboardResponse.TaskLogDetail.builder()
                    .action(logDoc.getAction())
                    .taskTitle(logDoc.getTaskTitle())
                    .executedBy(logDoc.getUsername())
                    .timestamp(logDoc.getTimestamp().toString())
                    .build());
        });

        return AuditLogDashboardResponse.builder()
                .totalActionsLogged(totalLogs)
                .systemStatus("HEALTHY - CONNECTED TO ELASTICSEARCH")
                .recentLogs(details)
                .build();
    }
}