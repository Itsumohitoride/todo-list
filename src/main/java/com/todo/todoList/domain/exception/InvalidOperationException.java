package com.todo.todoList.domain.exception;

/**
 * Exception thrown when an invalid operation is attempted
 */
public class InvalidOperationException extends DomainException {

    public InvalidOperationException(String message) {
        super(message);
    }

    public InvalidOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
