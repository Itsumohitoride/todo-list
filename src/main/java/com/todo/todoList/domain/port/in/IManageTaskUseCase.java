package com.todo.todoList.domain.port.in;

import com.todo.todoList.domain.enums.Status;
import com.todo.todoList.domain.enums.TaskType;
import com.todo.todoList.domain.model.Task;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Port (Interface) for Task Use Cases - Inbound
 */
public interface IManageTaskUseCase {
    Task createTask(Task task, UUID todoListId);
    Task updateTask(UUID id, Task task);
    void deleteTask(UUID id);
    Optional<Task> getTaskById(UUID id);
    List<Task> getTasksByTodoListId(UUID todoListId);
    List<Task> getTasksByType(TaskType type);
    Task markAsCompleted(UUID id);
    Task markAsPending(UUID id);

    // Search and filter methods
    List<Task> searchByDescription(String searchTerm);
    List<Task> searchByTodoListIdAndDescription(UUID todoListId, String searchTerm);
    List<Task> filterByTodoListIdAndStatus(UUID todoListId, Status status);
    List<Task> filterByTodoListIdAndDateRange(UUID todoListId, LocalDate startDate, LocalDate endDate);
    List<Task> getOverdueTasks(UUID todoListId);
}
