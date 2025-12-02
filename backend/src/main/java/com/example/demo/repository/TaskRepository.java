package com.example.demo.repository;

import com.example.demo.model.Task;
import com.example.demo.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    List<Task> findByProjectId(Long projectId);
    
    List<Task> findBySprintId(Long sprintId);
    
    List<Task> findByAssigneeId(Long assigneeId);
    
    List<Task> findByStatus(TaskStatus status);
    
    List<Task> findByAssigneeIdAndStatus(Long assigneeId, TaskStatus status);
}
