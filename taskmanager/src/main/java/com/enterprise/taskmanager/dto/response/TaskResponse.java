package com.enterprise.taskmanager.dto.response;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class TaskResponse implements Serializable { // WAJIB implement Serializable
    private static final long serialVersionUID = 1L;

    private Long id;
    private String title;
    private String status;
    private String username; // Hanya ambil namanya saja
}
