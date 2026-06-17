package com.enterprise.taskmanager.model.dokumen;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "task-audit-logs")
public class TaskAuditLog {

    @Id
    private String id;

    @Field(type = FieldType.Text)
    private String action; // E.g., "CREATE_TASK"

    @Field(type = FieldType.Long)
    private Long taskId;

    @Field(type = FieldType.Text)
    private String taskTitle;

    @Field(type = FieldType.Text)
    private String username;

    @Field(type = FieldType.Date, format = DateFormat.date_time)
    private Instant timestamp;
}
