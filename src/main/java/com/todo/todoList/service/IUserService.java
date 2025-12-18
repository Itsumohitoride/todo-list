package com.todo.todoList.service;

import com.todo.todoList.dto.UserDTO;

public interface IUserService {
    /**
     * Creates a new user in the system.
     *
     * @param userDTO an object containing the data required to create the user
     * @return the created user with generated identifiers and persisted data
     * @throws IllegalArgumentException if the provided user data is invalid
     */
    UserDTO createUser(UserDTO userDTO);

    /**
     * Updates an existing user in the system.
     *
     * @param userDTO an object containing the updated user data; it must include
     *                a valid identifier of an existing user
     * @return the updated user with persisted changes
     * @throws IllegalArgumentException if the user does not exist or the data is invalid
     */
    UserDTO updateUser(UserDTO userDTO);

    /**
     * Deletes an existing user from the system.
     *
     * @param userDTO an object identifying the user to be deleted; it must include
     *                a valid user identifier
     * @return the deleted user data, typically representing the state before deletion
     * @throws IllegalArgumentException if the user does not exist or cannot be deleted
     */
    UserDTO deleteUser(UserDTO userDTO);
}
