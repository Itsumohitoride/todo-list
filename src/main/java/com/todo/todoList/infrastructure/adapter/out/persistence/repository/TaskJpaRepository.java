package com.todo.todoList.infrastructure.adapter.out.persistence.repository;

import com.todo.todoList.domain.enums.Status;
import com.todo.todoList.domain.enums.TaskType;
import com.todo.todoList.infrastructure.adapter.out.persistence.entity.TaskJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA Repository for Task
 */
@Repository
public interface TaskJpaRepository extends JpaRepository<TaskJpaEntity, UUID> {
    List<TaskJpaEntity> findByListId(UUID listId);
    List<TaskJpaEntity> findByType(TaskType type);

    // Search methods
    List<TaskJpaEntity> findByDescriptionContainingIgnoreCase(String searchTerm);

    @Query("SELECT t FROM TaskJpaEntity t WHERE t.list.id = :listId AND LOWER(t.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<TaskJpaEntity> findByListIdAndDescriptionContaining(@Param("listId") UUID listId, @Param("searchTerm") String searchTerm);

    @Query("SELECT t FROM TaskJpaEntity t WHERE t.list.id = :listId AND t.status = :status")
    List<TaskJpaEntity> findByListIdAndStatus(@Param("listId") UUID listId, @Param("status") Status status);

    @Query("SELECT t FROM TaskJpaEntity t WHERE t.list.id = :listId AND t.date BETWEEN :startDate AND :endDate")
    List<TaskJpaEntity> findByListIdAndDateBetween(@Param("listId") UUID listId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT t FROM TaskJpaEntity t WHERE t.list.id = :listId AND t.status = 'PENDING' AND t.date < CURRENT_DATE")
    List<TaskJpaEntity> findOverdueTasksByListId(@Param("listId") UUID listId);
}
