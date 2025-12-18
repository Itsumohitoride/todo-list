package com.todo.todoList.model;

import com.todo.todoList.model.enums.Status;
import com.todo.todoList.model.enums.TaskType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String description;
    private Status status;
    private TaskType type;
    private LocalDate date;
    private LocalDate completed;
    @OneToOne
    private TodoList list;
}
