package com.todo.todoList.infrastructure.adapter.out.persistence.entity;

import com.todo.todoList.domain.enums.ListType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * TodoList JPA Entity - Infrastructure persistence model
 */
@Entity
@Table(name = "todo_lists")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TodoListJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String color;

    @OneToMany(mappedBy = "list", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TaskJpaEntity> tasks = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "list_type", nullable = false)
    private ListType listType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserJpaEntity user;
}
