package com.todo.todoList.model;

import com.todo.todoList.model.enums.ListType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class TodoList {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private Long id;
    private String name;
    private String color;
    @OneToMany
    private List<Task> tasks;
    private ListType listType;
    @OneToOne
    private User user;
}
