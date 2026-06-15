package com.enterprise.taskmanager.repository;

import com.enterprise.taskmanager.model.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    // Ini otomatis bisa nge-query berdasarkan user_id
    List<Task> findByUserId(Long userId);
}
