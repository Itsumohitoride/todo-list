package com.todo.todoList.dto;

import com.todo.todoList.model.enums.ListType;

import java.util.UUID;

public record TodoListDTO(
    String name,
    String color,
    ListType listType,
    UUID userId
) { }
