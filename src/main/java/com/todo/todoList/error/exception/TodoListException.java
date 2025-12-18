package com.todo.todoList.error.exception;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@Builder
public class TodoListException extends RuntimeException {
    private final HttpStatus error;

    public TodoListException(String message, HttpStatus error) {
        super(message);
        this.error = error;
    }
}
