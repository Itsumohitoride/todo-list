package com.todo.todoList.service;

import com.todo.todoList.dto.TodoListDTO;

public interface ITodoListService {
    /**
     * Creates a new to-do list in the system.
     *
     * @param todoListDTO an object containing the data required to create the to-do list
     * @return the created to-do list with generated identifiers and persisted data
     * @throws IllegalArgumentException if the provided to-do list data is invalid
     */
    TodoListDTO createTodoList(TodoListDTO todoListDTO);

    /**
     * Updates an existing to-do list in the system.
     *
     * @param todoListDTO an object containing the updated to-do list data; it must include
     *                    a valid identifier of an existing to-do list
     * @return the updated to-do list with persisted changes
     * @throws IllegalArgumentException if the to-do list does not exist or the data is invalid
     */
    TodoListDTO updateTodoList(TodoListDTO todoListDTO);

    /**
     * Deletes an existing to-do list from the system.
     *
     * @param todoListDTO an object identifying the to-do list to be deleted; it must include
     *                    a valid to-do list identifier
     * @return the deleted to-do list data, typically representing the state before deletion
     * @throws IllegalArgumentException if the to-do list does not exist or cannot be deleted
     */
    TodoListDTO deleteTodoList(TodoListDTO todoListDTO);
}
