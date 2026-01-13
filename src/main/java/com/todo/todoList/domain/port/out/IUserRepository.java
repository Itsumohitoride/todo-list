package com.todo.todoList.domain.port.out;

import com.todo.todoList.domain.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Port (Interface) for User Repository - Outbound
 * This interface is implemented by the infrastructure layer
 */
public interface IUserRepository {
    User save(User user);
    Optional<User> findById(UUID id);
    List<User> findAll();
    void deleteById(UUID id);
    boolean existsById(UUID id);
    Optional<User> findByEmail(String email);
    Optional<User> findByNickname(String nickname);
}
