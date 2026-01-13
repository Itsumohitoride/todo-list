package com.todo.todoList.application.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.todo.todoList.domain.enums.ListType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "TodoList Data Transfer Object")
public class TodoListDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "TodoList unique identifier (auto-generated)", accessMode = Schema.AccessMode.READ_ONLY)
    private UUID id;

    @NotBlank(message = "Name is required")
    private String name;

    private String color;

    @NotNull(message = "List type is required")
    private ListType listType;

    private UUID userId;
}
