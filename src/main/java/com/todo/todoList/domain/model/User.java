package com.todo.todoList.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * User domain entity - Pure domain model without framework dependencies
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String nickname;
    private Image profilePicture;
    @Builder.Default
    private List<TodoList> lists = new ArrayList<>();

    // Business logic methods can be added here
    public void addList(TodoList todoList) {
        if (todoList == null) {
            return;
        }
        if (this.lists == null) {
            this.lists = new ArrayList<>();
        }
        this.lists.add(todoList);
    }

    public void removeList(TodoList todoList) {
        if (this.lists != null) {
            this.lists.remove(todoList);
        }
    }
}
