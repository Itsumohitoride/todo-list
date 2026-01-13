package com.todo.todoList.domain.exception;

/**
 * Exception thrown when trying to create an entity that already exists
 */
public class DuplicateEntityException extends DomainException {

    public DuplicateEntityException(String field, String value) {
        super(String.format("%s '%s' already exists", field, value));
    }

    public DuplicateEntityException(String message) {
        super(message);
    }
}
