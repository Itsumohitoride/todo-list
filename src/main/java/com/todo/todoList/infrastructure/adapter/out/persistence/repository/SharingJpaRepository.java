package com.todo.todoList.infrastructure.adapter.out.persistence.repository;

import com.todo.todoList.infrastructure.adapter.out.persistence.entity.SharingJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA Repository for Sharing
 */
@Repository
public interface SharingJpaRepository extends JpaRepository<SharingJpaEntity, UUID> {
    Optional<SharingJpaEntity> findByListId(UUID listId);
}
