package com.enterprise.taskmanager.service;

import com.enterprise.taskmanager.dto.request.TaskRequest;
import com.enterprise.taskmanager.dto.response.AuditLogDashboardResponse;
import com.enterprise.taskmanager.dto.response.TaskResponse;
import com.enterprise.taskmanager.model.dokumen.TaskAuditLog;
import com.enterprise.taskmanager.model.entity.Task;
import com.enterprise.taskmanager.model.entity.User;
import com.enterprise.taskmanager.model.enums.TaskStatus;
import com.enterprise.taskmanager.repository.TaskRepository;
import com.enterprise.taskmanager.repository.UserRepository;
import com.enterprise.taskmanager.repository.elastic.TaskAuditLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoSettings;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TaskAuditLogRepository taskAuditLogRepository;

    @InjectMocks
    private TaskService taskService;

    private User testUser;
    private Task task1;
    private Task task2;

    @BeforeEach
    void setUp() {
        testUser = mock(User.class);
        when(testUser.getId()).thenReturn(1L);
        when(testUser.getUsername()).thenReturn("irvansyah_dev");

        task1 = mock(Task.class);
        when(task1.getId()).thenReturn(101L);
        when(task1.getTitle()).thenReturn("Buy groceries");
        when(task1.getStatus()).thenReturn(TaskStatus.TODO);
        when(task1.getUser()).thenReturn(testUser);

        task2 = mock(Task.class);
        when(task2.getId()).thenReturn(102L);
        when(task2.getTitle()).thenReturn("Finish report");
        when(task2.getStatus()).thenReturn(TaskStatus.IN_PROGRESS);
        when(task2.getUser()).thenReturn(testUser);
    }

    // =====================================================================
    // TEST FOR: getTasksByUser
    // =====================================================================
    @Test
    void getTasksByUser_shouldReturnListOfTaskResponses_whenTasksExist() {
        Long userId = testUser.getId();
        // Sesuai dengan repositori yang Mas pakai (findByUserId atau findByUserIdNative)
        when(taskRepository.findByUserId(userId)).thenReturn(Arrays.asList(task1, task2));

        List<TaskResponse> result = taskService.getTasksByUser(userId);

        assertEquals(2, result.size());
        assertEquals(task1.getId(), result.get(0).getId());
        assertEquals(task1.getTitle(), result.get(0).getTitle());
        assertEquals(testUser.getUsername(), result.get(0).getUsername());
    }

    @Test
    void getTasksByUser_shouldReturnEmptyList_whenNoTasksExist() {
        Long userId = testUser.getId();
        when(taskRepository.findByUserId(userId)).thenReturn(Collections.emptyList());

        List<TaskResponse> result = taskService.getTasksByUser(userId);

        assertTrue(result.isEmpty());
    }

    // =====================================================================
    // TEST FOR: createTask (Skenario Sukses)
    // =====================================================================
    @Test
    void createTask_shouldSaveTaskAndLogToElasticsearch_whenRequestIsValid() {
        // Given
        TaskRequest request = new TaskRequest();
        request.setUserId(1L);
        request.setTitle("Implementasi Arsitektur Kafka dan Redis di Cluster BMRI");
        request.setStatus("IN_PROGRESS");

        Task savedTask = Task.builder()
                .id(1L)
                .title(request.getTitle())
                .status(TaskStatus.IN_PROGRESS)
                .user(testUser)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

        // When
        TaskResponse response = taskService.createTask(request);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Implementasi Arsitektur Kafka dan Redis di Cluster BMRI", response.getTitle());
        assertEquals("irvansyah_dev", response.getUsername());

        // Pastikan audit log Elasticsearch juga ikut terpanggil untuk disimpan
        verify(taskAuditLogRepository, times(1)).save(any(TaskAuditLog.class));
    }

    // =====================================================================
    // TEST FOR: createTask Fault Tolerance (Elasticsearch Mati / Eror)
    // =====================================================================
    @Test
    void createTask_shouldStillSucceed_whenElasticsearchThrowsException() {
        // Given
        TaskRequest request = new TaskRequest();
        request.setUserId(1L);
        request.setTitle("Optimasi Query Cluster Bank Mandiri");
        request.setStatus("IN_PROGRESS");

        Task savedTask = Task.builder()
                .id(2L)
                .title(request.getTitle())
                .status(TaskStatus.IN_PROGRESS)
                .user(testUser)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

        // Simulasikan Elasticsearch cluster sedang down/error
        when(taskAuditLogRepository.save(any(TaskAuditLog.class)))
                .thenThrow(new RuntimeException("Elasticsearch cluster down"));

        // When & Then
        // Transaksi Postgres HARUS tetap berhasil berjalan (tidak boleh crash bagi user)
        assertDoesNotThrow(() -> {
            TaskResponse response = taskService.createTask(request);
            assertEquals(2L, response.getId());
            assertEquals("irvansyah_dev", response.getUsername());
        });
    }

    // =====================================================================
    // TEST FOR: getElasticsearchAnalytics (Dashboard)
    // =====================================================================
    @Test
    void getElasticsearchAnalytics_shouldReturnDashboardSummary_whenDataExists() {
        // Given
        TaskAuditLog mockLog = TaskAuditLog.builder()
                .id("elastic-id-1")
                .action("CREATE_TASK")
                .taskId(1L)
                .taskTitle("Test Task Elastic")
                .username("irvansyah_dev")
                .timestamp(Instant.now())
                .build();

        when(taskAuditLogRepository.count()).thenReturn(1L);
        when(taskAuditLogRepository.findAll()).thenReturn(Arrays.asList(mockLog));

        // When
        AuditLogDashboardResponse dashboard = taskService.getElasticsearchAnalytics();

        // Then
        assertNotNull(dashboard);
        assertEquals(1L, dashboard.getTotalActionsLogged());
        assertEquals("HEALTHY - CONNECTED TO ELASTICSEARCH", dashboard.getSystemStatus());
        assertEquals(1, dashboard.getRecentLogs().size());
        assertEquals("Test Task Elastic", dashboard.getRecentLogs().get(0).getTaskTitle());
        assertEquals("irvansyah_dev", dashboard.getRecentLogs().get(0).getExecutedBy());
    }
}