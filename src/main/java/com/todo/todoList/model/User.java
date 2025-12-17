package com.todo.todoList.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.awt.Image;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class User {
    @id
    @GeneratedValue(strategy = GenerationType.UUID)
    private Long  id;
    private String firstName;
    private String lastName;
    private String email;
    private String nickname;
    private Image profilePicture;
    @OneToMany
    private List<TodoList> lists;
}
