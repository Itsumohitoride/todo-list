package com.todo.todoList.application.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "User Statistics Data Transfer Object")
public class StatisticsDTO {
    @Schema(description = "Statistics unique identifier", accessMode = Schema.AccessMode.READ_ONLY)
    private UUID id;

    @Schema(description = "Total number of todo lists", example = "5")
    private int totalLists;

    @Schema(description = "Total number of tasks", example = "20")
    private int totalTasks;

    @Schema(description = "Number of completed tasks", example = "12")
    private int completedTasks;

    @Schema(description = "Number of pending tasks", example = "8")
    private int pendingTasks;

    @Schema(description = "Completion percentage", example = "60.0")
    private double completionPercentage;

    @Schema(description = "When statistics were calculated")
    private LocalDateTime calculatedAt;

    @Schema(description = "User ID these statistics belong to")
    private UUID userId;
}
