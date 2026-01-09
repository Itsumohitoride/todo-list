package com.todo.todoList.infrastructure.adapter.out.persistence.entity;

import com.todo.todoList.domain.enums.Status;
import com.todo.todoList.domain.enums.TaskType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Task JPA Entity - Infrastructure persistence model
 */
@Entity
@Table(name = "tasks")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Enumerated(EnumType.STRING)
    private TaskType type;

    @Column(name = "due_date")
    private LocalDate date;

    @Column(name = "completed_date")
    private LocalDate completed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "todo_list_id", nullable = false)
    private TodoListJpaEntity list;
}
