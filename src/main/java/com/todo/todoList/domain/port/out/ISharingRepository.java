package com.todo.todoList.domain.port.out;

import com.todo.todoList.domain.model.Sharing;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Port (Interface) for Sharing Repository - Outbound
 * This interface is implemented by the infrastructure layer
 */
public interface ISharingRepository {
    Sharing save(Sharing sharing);
    Optional<Sharing> findById(UUID id);
    Optional<Sharing> findByTodoListId(UUID todoListId);
    List<Sharing> findAll();
    void deleteById(UUID id);
    boolean existsById(UUID id);
}
