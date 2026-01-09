package com.todo.todoList.domain.model;

import com.todo.todoList.domain.enums.Status;
import com.todo.todoList.domain.enums.TaskType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Task domain entity - Pure domain model without framework dependencies
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Task {
    private UUID id;
    private String description;
    private Status status;
    private TaskType type;
    private LocalDate date;
    private LocalDate completed;
    private TodoList list;

    // Business logic methods
    public void markAsCompleted() {
        this.status = Status.COMPLETED;
        this.completed = LocalDate.now();
    }

    public void markAsPending() {
        this.status = Status.PENDING;
        this.completed = null;
    }

    public boolean isCompleted() {
        return Status.COMPLETED.equals(this.status);
    }

    public boolean isOverdue() {
        if (this.date == null || isCompleted()) {
            return false;
        }
        return LocalDate.now().isAfter(this.date);
    }
}
