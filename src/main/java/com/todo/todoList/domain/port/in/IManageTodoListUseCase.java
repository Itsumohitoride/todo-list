package com.todo.todoList.domain.port.in;

import com.todo.todoList.domain.model.TodoList;

import java.util.List;
import java.util.UUID;

/**
 * Port (Interface) for TodoList Use Cases - Inbound
 */
public interface IManageTodoListUseCase {
    TodoList createTodoList(TodoList todoList);
    TodoList updateTodoList(UUID id, TodoList todoList);
    void deleteTodoList(UUID id);
    TodoList getTodoListById(UUID id);
    List<TodoList> getTodoListsByUserId(UUID userId);
    List<TodoList> getAllTodoLists();
    TodoList changeColor(UUID id, String color);

    // Search methods
    List<TodoList> searchByName(String searchTerm);
    List<TodoList> searchByUserIdAndName(UUID userId, String searchTerm);
}
