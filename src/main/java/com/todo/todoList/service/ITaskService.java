package com.todo.todoList.service;

import com.todo.todoList.dto.TaskDTO;

public interface ITaskService {
    /**
     * Creates a new task in the system.
     *
     * @param taskDTO an object containing the data required to create the task
     * @return the created task with generated identifiers and persisted data
     * @throws IllegalArgumentException if the provided task data is invalid
     */
    TaskDTO createTask(TaskDTO taskDTO);

    /**
     * Updates an existing task in the system.
     *
     * @param taskDTO an object containing the updated task data; it must include
     *                a valid identifier of an existing task
     * @return the updated task with persisted changes
     * @throws IllegalArgumentException if the task does not exist or the data is invalid
     */
    TaskDTO updateTask(TaskDTO taskDTO);

    /**
     * Deletes an existing task from the system.
     *
     * @param taskDTO an object identifying the task to be deleted; it must include
     *                a valid task identifier
     * @return the deleted task data, typically representing the state before deletion
     * @throws IllegalArgumentException if the task does not exist or cannot be deleted
     */
    TaskDTO deleteTask(TaskDTO taskDTO);
}
