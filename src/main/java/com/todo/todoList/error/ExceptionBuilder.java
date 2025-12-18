package com.todo.todoList.error;

import com.todo.todoList.error.exception.TodoListException;
import org.springframework.http.HttpStatus;

public class ExceptionBuilder {
    public TodoListException duplicatedValueException(String message, String... fields) {
        return new TodoListException(
                String.format(message, (Object[]) fields),
                HttpStatus.CONFLICT
        );
    }
}
