package com.enterprise.taskmanager.repository.elastic;


import com.enterprise.taskmanager.model.dokumen.TaskAuditLog;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskAuditLogRepository extends ElasticsearchRepository<TaskAuditLog, String> {
}