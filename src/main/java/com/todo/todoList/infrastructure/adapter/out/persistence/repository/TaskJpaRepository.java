package com.todo.todoList.infrastructure.adapter.out.persistence.repository;

import com.todo.todoList.domain.enums.TaskType;
import com.todo.todoList.infrastructure.adapter.out.persistence.entity.TaskJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA Repository for Task
 */
@Repository
public interface TaskJpaRepository extends JpaRepository<TaskJpaEntity, UUID> {
    List<TaskJpaEntity> findByListId(UUID listId);
    List<TaskJpaEntity> findByType(TaskType type);
}
