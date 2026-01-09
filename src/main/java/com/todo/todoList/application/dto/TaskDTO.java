package com.todo.todoList.application.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.todo.todoList.domain.enums.Status;
import com.todo.todoList.domain.enums.TaskType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Task Data Transfer Object")
public class TaskDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "Task unique identifier (auto-generated)", accessMode = Schema.AccessMode.READ_ONLY)
    private UUID id;

    @NotBlank(message = "Description is required")
    @Schema(description = "Task description", example = "Buy groceries")
    private String description;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "Task status (auto-managed)", accessMode = Schema.AccessMode.READ_ONLY)
    private Status status;

    @NotNull(message = "Task type is required")
    @Schema(description = "Task type", example = "NORMAL")
    private TaskType type;

    @Schema(description = "Task due date", example = "2026-01-15")
    private LocalDate date;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "Date when task was completed", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDate completed;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "ID of the TodoList this task belongs to", accessMode = Schema.AccessMode.READ_ONLY)
    private UUID todoListId;
}
