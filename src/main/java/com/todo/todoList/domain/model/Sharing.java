package com.todo.todoList.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Sharing domain entity - Pure domain model without framework dependencies
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Sharing {
    private UUID id;
    private TodoList list;
    @Builder.Default
    private List<User> users = new ArrayList<>();

    // Business logic methods
    public void addUser(User user) {
        if (user == null) {
            return;
        }
        if (this.users == null) {
            this.users = new ArrayList<>();
        }
        if (!this.users.contains(user)) {
            this.users.add(user);
        }
    }

    public void removeUser(User user) {
        if (this.users != null) {
            this.users.remove(user);
        }
    }

    public boolean hasUser(User user) {
        return this.users != null && this.users.contains(user);
    }

    public int getUserCount() {
        return this.users != null ? this.users.size() : 0;
    }
}
