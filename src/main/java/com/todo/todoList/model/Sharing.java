package com.todo.todoList.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Sharing {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private Long id;
    @OneToOne
    private TodoList list;
    @OneToMany
    private List<User> users;
}
