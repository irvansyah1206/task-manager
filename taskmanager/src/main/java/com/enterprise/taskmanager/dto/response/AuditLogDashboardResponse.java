package com.enterprise.taskmanager.dto.response;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class AuditLogDashboardResponse {
    private long totalActionsLogged;
    private String systemStatus;
    private List<TaskLogDetail> recentLogs;

    @Data
    @Builder
    public static class TaskLogDetail {
        private String action;
        private String taskTitle;
        private String executedBy;
        private String timestamp;
    }
}