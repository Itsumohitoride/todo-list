package com.todo.todoList.domain.port.out;

import com.todo.todoList.domain.model.TodoList;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Port (Interface) for TodoList Repository - Outbound
 * This interface is implemented by the infrastructure layer
 */
public interface ITodoListRepository {
    TodoList save(TodoList todoList);
    Optional<TodoList> findById(UUID id);
    List<TodoList> findAll();
    List<TodoList> findByUserId(UUID userId);
    void deleteById(UUID id);
    boolean existsById(UUID id);

    // Search methods
    List<TodoList> searchByNameContaining(String searchTerm);
    List<TodoList> searchByUserIdAndNameContaining(UUID userId, String searchTerm);
}
