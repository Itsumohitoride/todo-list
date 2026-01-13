package com.todo.todoList.infrastructure.mapper;

import com.todo.todoList.domain.model.Sharing;
import com.todo.todoList.domain.model.TodoList;
import com.todo.todoList.domain.model.User;
import com.todo.todoList.infrastructure.adapter.out.persistence.entity.SharingJpaEntity;
import com.todo.todoList.infrastructure.adapter.out.persistence.entity.TodoListJpaEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper between Sharing domain model and SharingJpaEntity
 */
@Component
public class SharingMapper {

    public Sharing toDomain(SharingJpaEntity entity) {
        if (entity == null) return null;

        return Sharing.builder()
                .id(entity.getId())
                .shareToken(entity.getShareToken())
                .createdAt(entity.getCreatedAt())
                .list(entity.getList() != null ?
                        TodoList.builder()
                                .id(entity.getList().getId())
                                .name(entity.getList().getName())
                                .color(entity.getList().getColor())
                                .listType(entity.getList().getListType())
                                .build()
                        : null)
                .users(entity.getUsers() != null ?
                        entity.getUsers().stream()
                                .map(userEntity -> User.builder()
                                        .id(userEntity.getId())
                                        .email(userEntity.getEmail())
                                        .nickname(userEntity.getNickname())
                                        .firstName(userEntity.getFirstName())
                                        .lastName(userEntity.getLastName())
                                        .build())
                                .collect(Collectors.toList())
                        : null)
                .build();
    }

    public SharingJpaEntity toJpaEntity(Sharing domain, TodoListJpaEntity listEntity) {
        if (domain == null) return null;

        return SharingJpaEntity.builder()
                .id(domain.getId())
                .shareToken(domain.getShareToken())
                .createdAt(domain.getCreatedAt())
                .list(listEntity)
                .build();
    }

    public void updateJpaEntityFromDomain(Sharing domain, SharingJpaEntity entity) {
        // Sharing entity primarily manages relationships, not much to update
        // The list relationship is typically not changed after creation
        // User relationships are managed separately via addUser/removeUser
    }

    /**
     * Maps a list of JPA entities to domain models
     */
    public List<Sharing> toDomainList(List<SharingJpaEntity> entities) {
        if (entities == null) return null;
        return entities.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    /**
     * Maps a domain model to JPA entity without relationships (for avoiding circular references)
     */
    public Sharing toDomainShallow(SharingJpaEntity entity) {
        if (entity == null) return null;

        return Sharing.builder()
                .id(entity.getId())
                .build();
    }
}
