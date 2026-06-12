package com.enterprise.taskmanager.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TaskResponse {
    private Long id;
    private String title;
    private String status;
    private String username; // Hanya ambil namanya saja
}
