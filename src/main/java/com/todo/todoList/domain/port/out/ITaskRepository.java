package com.todo.todoList.domain.port.out;

import com.todo.todoList.domain.enums.TaskType;
import com.todo.todoList.domain.model.Task;

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
}
