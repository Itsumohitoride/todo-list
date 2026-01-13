package com.todo.todoList.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Statistics domain entity - Pure domain model without framework dependencies
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Statistics {
    private UUID id;
    private int totalLists;
    private int totalTasks;
    private int completedTasks;
    private int pendingTasks;
    private LocalDateTime calculatedAt;
    private User user;

    // Business logic methods

    /**
     * Calculates the completion percentage
     * @return percentage (0-100) or 0 if no tasks exist
     */
    public double getCompletionPercentage() {
        if (totalTasks == 0) {
            return 0.0;
        }
        return (completedTasks * 100.0) / totalTasks;
    }

    /**
     * Checks if user has any tasks
     * @return true if user has at least one task
     */
    public boolean hasTasks() {
        return totalTasks > 0;
    }

    /**
     * Checks if all tasks are completed
     * @return true if all tasks are completed
     */
    public boolean allTasksCompleted() {
        return totalTasks > 0 && completedTasks == totalTasks;
    }
}
