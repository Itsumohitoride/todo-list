package com.todo.todoList.application.dto;

import com.todo.todoList.domain.enums.ListType;

import java.util.UUID;

public record TodoListDTO(
    String name,
    String color,
    ListType listType,
    UUID userId
) { }
