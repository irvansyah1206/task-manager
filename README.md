curl :
1. curl -X POST http://localhost:8081/api/tasks \
     -H "Content-Type: application/json" \
     -d '{
           "title": "Implementasi Arsitektur Kafka dan Redis di Cluster BMRI",
           "status": "IN_PROGRESS",
           "userId": 1
         }'

 2. curl -X GET http://localhost:8081/api/tasks/user/1

 3. curl -X GET http://localhost:8081/api/tasks/analytics/dashboard \
         -H "Accept: application/json"

 4. postman request 'http://localhost:9200/task-audit-logs/_search?pretty=null'