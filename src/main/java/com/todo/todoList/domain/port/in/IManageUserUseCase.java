package com.todo.todoList.domain.port.in;

import com.todo.todoList.domain.model.User;

import java.util.List;
import java.util.UUID;

/**
 * Port (Interface) for User Use Cases - Inbound
 */
public interface IManageUserUseCase {
    User createUser(User user);
    User updateUser(UUID id, User user);
    void deleteUser(UUID id);
    User getUserById(UUID id);
    List<User> getAllUsers();
    User getUserByEmail(String email);
    User getUserByNickname(String nickname);
}
