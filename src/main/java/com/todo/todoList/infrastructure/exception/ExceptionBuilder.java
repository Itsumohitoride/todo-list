package com.todo.todoList.infrastructure.exception;

import org.springframework.http.HttpStatus;

public class ExceptionBuilder {
    public TodoListException duplicatedValueException(String message, String... fields) {
        return new TodoListException(
                String.format(message, (Object[]) fields),
                HttpStatus.CONFLICT
        );
    }
}
