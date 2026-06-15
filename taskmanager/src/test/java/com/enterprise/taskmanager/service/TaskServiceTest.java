package com.enterprise.taskmanager.service;

import com.enterprise.taskmanager.dto.response.TaskResponse;
import com.enterprise.taskmanager.model.entity.Task;
import com.enterprise.taskmanager.model.entity.User;
import com.enterprise.taskmanager.model.enums.TaskStatus; // Correct and confirmed import
import com.enterprise.taskmanager.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoSettings;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT) // To handle UnnecessaryStubbingException
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private User testUser;
    private Task task1;
    private Task task2;

    @BeforeEach
    void setUp() {
        testUser = mock(User.class);
        when(testUser.getId()).thenReturn(1L);
        when(testUser.getUsername()).thenReturn("testuser");

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

    @Test
    void testDummyTask() {
        String status = "SUCCESS";
        assertEquals("SUCCESS", status); // Corrected assertEquals usage
    }

    @Test
    void getTasksByUser_shouldReturnListOfTaskResponses_whenTasksExist() {
        // Given
        Long userId = testUser.getId();
        when(taskRepository.findByUserId(userId)).thenReturn(Arrays.asList(task1, task2));

        // When
        List<TaskResponse> result = taskService.getTasksByUser(userId);

        // Then
        assertEquals(2, result.size());
        assertEquals(task1.getId(), result.get(0).getId());
        assertEquals(task1.getTitle(), result.get(0).getTitle());
        assertEquals(task1.getStatus().name(), result.get(0).getStatus());
        assertEquals(testUser.getUsername(), result.get(0).getUsername());

        assertEquals(task2.getId(), result.get(1).getId());
        assertEquals(task2.getTitle(), result.get(1).getTitle());
        assertEquals(task2.getStatus().name(), result.get(1).getStatus());
        assertEquals(testUser.getUsername(), result.get(1).getUsername());
    }

    @Test
    void getTasksByUser_shouldReturnEmptyList_whenNoTasksExist() {
        // Given
        Long userId = testUser.getId();
        when(taskRepository.findByUserId(userId)).thenReturn(Collections.emptyList());

        // When
        List<TaskResponse> result = taskService.getTasksByUser(userId);

        // Then
        assertTrue(result.isEmpty());
    }
}
