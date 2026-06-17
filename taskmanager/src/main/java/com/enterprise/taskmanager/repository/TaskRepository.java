package com.enterprise.taskmanager.repository;

import com.enterprise.taskmanager.model.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    // Ini otomatis bisa nge-query berdasarkan user_id
    List<Task> findByUserId(Long userId);

    @Query(value = "SELECT t.* FROM tasks t " +
            "INNER JOIN users u ON t.user_id = u.id " +
            "WHERE t.user_id = :userId",
            nativeQuery = true)
    List<Task> findByUserIdNative(@Param("userId") Long userId);
}
