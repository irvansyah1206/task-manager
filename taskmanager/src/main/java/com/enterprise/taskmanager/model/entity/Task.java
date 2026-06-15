package com.enterprise.taskmanager.model.entity;

import com.enterprise.taskmanager.model.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore; // Agar tidak terjadi circular reference

@Entity
@Table(name = "tasks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    // Join Column: Menghubungkan Task ke User
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore // Mencegah infinite loop saat di-convert ke JSON
    private User user;

}