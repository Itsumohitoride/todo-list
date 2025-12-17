package com.todo.todoList.model;

import com.todo.todoList.model.enums.Status;
import com.todo.todoList.model.enums.TaskType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private Long id;
    private String description;
    private Status status;
    private TaskType type;
    private LocalDateTime date;
    private LocalDateTime completed;
    @OneToOne
    private TodoList list;
}
