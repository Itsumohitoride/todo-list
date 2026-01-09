package com.todo.todoList.domain.port.in;

import com.todo.todoList.domain.enums.TaskType;
import com.todo.todoList.domain.model.Task;

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
}
