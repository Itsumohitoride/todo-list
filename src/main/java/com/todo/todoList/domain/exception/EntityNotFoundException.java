package com.todo.todoList.domain.exception;

import java.util.UUID;

/**
 * Exception thrown when an entity is not found in the repository
 */
public class EntityNotFoundException extends DomainException {

    public EntityNotFoundException(String entityName, UUID id) {
        super(String.format("%s with id %s not found", entityName, id));
    }

    public EntityNotFoundException(String message) {
        super(message);
    }
}
