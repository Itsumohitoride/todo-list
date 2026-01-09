package com.todo.todoList.infrastructure.adapter.out.persistence.repository;

import com.todo.todoList.infrastructure.adapter.out.persistence.entity.TodoListJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA Repository for TodoList
 */
@Repository
public interface TodoListJpaRepository extends JpaRepository<TodoListJpaEntity, UUID> {
    List<TodoListJpaEntity> findByUserId(UUID userId);

    // Search methods
    List<TodoListJpaEntity> findByNameContainingIgnoreCase(String searchTerm);

    @Query("SELECT t FROM TodoListJpaEntity t WHERE t.user.id = :userId AND LOWER(t.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<TodoListJpaEntity> findByUserIdAndNameContaining(@Param("userId") UUID userId, @Param("searchTerm") String searchTerm);
}
