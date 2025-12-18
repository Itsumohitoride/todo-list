package com.todo.todoList.dto;

import com.todo.todoList.model.enums.TaskType;

public record TaskDTO(
    String description,
    TaskType type
) { }
