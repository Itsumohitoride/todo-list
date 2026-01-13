package com.todo.todoList.domain.port.out;

import com.todo.todoList.domain.enums.Status;
import com.todo.todoList.domain.enums.TaskType;
import com.todo.todoList.domain.model.Task;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Port (Interface) for Task Repository - Outbound
 * This interface is implemented by the infrastructure layer
 */
public interface ITaskRepository {
    Task save(Task task);
    Optional<Task> findById(UUID id);
    List<Task> findAll();
    List<Task> findByTodoListId(UUID todoListId);
    List<Task> findByType(TaskType type);
    void deleteById(UUID id);
    boolean existsById(UUID id);

    // Search and filter methods
    List<Task> searchByDescriptionContaining(String searchTerm);
    List<Task> searchByTodoListIdAndDescriptionContaining(UUID todoListId, String searchTerm);
    List<Task> findByTodoListIdAndStatus(UUID todoListId, Status status);
    List<Task> findByTodoListIdAndDateBetween(UUID todoListId, LocalDate startDate, LocalDate endDate);
    List<Task> findOverdueTasks(UUID todoListId);
}
