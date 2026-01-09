package com.todo.todoList.domain.model;

import com.todo.todoList.domain.enums.ListType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * TodoList domain entity - Pure domain model without framework dependencies
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TodoList {
    private UUID id;
    private String name;
    private String color;
    @Builder.Default
    private List<Task> tasks = new ArrayList<>();
    private ListType listType;
    private User user;

    // Business logic methods
    public void addTask(Task task) {
        if (task == null) {
            return;
        }
        if (this.tasks == null) {
            this.tasks = new ArrayList<>();
        }
        this.tasks.add(task);
    }

    public void removeTask(Task task) {
        if (this.tasks != null) {
            this.tasks.remove(task);
        }
    }

    public int getTotalTasks() {
        return this.tasks != null ? this.tasks.size() : 0;
    }

    public long getCompletedTasksCount() {
        if (this.tasks == null) return 0;
        return this.tasks.stream()
                .filter(Task::isCompleted)
                .count();
    }
}
