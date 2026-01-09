package com.todo.todoList.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Sharing JPA Entity - Infrastructure persistence model
 */
@Entity
@Table(name = "sharings")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SharingJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "todo_list_id", unique = true, nullable = false)
    private TodoListJpaEntity list;

    @ManyToMany
    @JoinTable(
        name = "sharing_users",
        joinColumns = @JoinColumn(name = "sharing_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @Builder.Default
    private List<UserJpaEntity> users = new ArrayList<>();
}
