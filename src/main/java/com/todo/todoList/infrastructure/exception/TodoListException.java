package com.todo.todoList.infrastructure.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class TodoListException extends RuntimeException {
    private final HttpStatus error;

    public TodoListException(String message, HttpStatus error) {
        super(message);
        this.error = error;
    }
}
