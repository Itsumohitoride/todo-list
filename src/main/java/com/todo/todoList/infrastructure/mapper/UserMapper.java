package com.todo.todoList.infrastructure.mapper;

import com.todo.todoList.domain.model.User;
import com.todo.todoList.infrastructure.adapter.out.persistence.entity.UserJpaEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper between User domain model and UserJpaEntity
 */
@Component
public class UserMapper {

    public User toDomain(UserJpaEntity entity) {
        if (entity == null) return null;

        return User.builder()
                .id(entity.getId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .email(entity.getEmail())
                .nickname(entity.getNickname())
                .password(entity.getPassword())
                .role(entity.getRole())
                // profilePicture mapping would go here if needed
                .lists(entity.getLists() != null ?
                        entity.getLists().stream()
                                .map(list -> {
                                    // Shallow mapping to avoid circular references
                                    return com.todo.todoList.domain.model.TodoList.builder()
                                            .id(list.getId())
                                            .name(list.getName())
                                            .color(list.getColor())
                                            .listType(list.getListType())
                                            .build();
                                })
                                .collect(Collectors.toList())
                        : null)
                .build();
    }

    public UserJpaEntity toJpaEntity(User domain) {
        if (domain == null) return null;

        return UserJpaEntity.builder()
                .id(domain.getId())
                .firstName(domain.getFirstName())
                .lastName(domain.getLastName())
                .email(domain.getEmail())
                .nickname(domain.getNickname())
                .password(domain.getPassword())
                .role(domain.getRole())
                .profilePictureUrl(domain.getProfilePicture() != null ? "" : null) // Convert Image to URL string
                .build();
    }

    public void updateJpaEntityFromDomain(User domain, UserJpaEntity entity) {
        entity.setFirstName(domain.getFirstName());
        entity.setLastName(domain.getLastName());
        entity.setEmail(domain.getEmail());
        entity.setNickname(domain.getNickname());
        entity.setRole(domain.getRole());
        // Note: Password is not updated here for security reasons - use a separate method
    }

    /**
     * Maps a list of JPA entities to domain models
     */
    public List<User> toDomainList(List<UserJpaEntity> entities) {
        if (entities == null) return null;
        return entities.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    /**
     * Maps a domain model to JPA entity without relationships (for avoiding circular references)
     */
    public User toDomainShallow(UserJpaEntity entity) {
        if (entity == null) return null;

        return User.builder()
                .id(entity.getId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .email(entity.getEmail())
                .nickname(entity.getNickname())
                .password(entity.getPassword())
                .role(entity.getRole())
                .build();
    }
}
