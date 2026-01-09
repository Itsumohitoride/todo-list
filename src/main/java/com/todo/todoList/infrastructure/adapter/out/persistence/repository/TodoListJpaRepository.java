package com.todo.todoList.infrastructure.adapter.out.persistence.repository;

import com.todo.todoList.infrastructure.adapter.out.persistence.entity.TodoListJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA Repository for TodoList
 */
@Repository
public interface TodoListJpaRepository extends JpaRepository<TodoListJpaEntity, UUID> {
    List<TodoListJpaEntity> findByUserId(UUID userId);
}
