package com.todo.todoList.application.dto;

import com.todo.todoList.domain.enums.TaskType;

public record TaskDTO(
    String description,
    TaskType type
) { }
