package com.todo.todoList.infrastructure.mapper;

import com.todo.todoList.domain.model.TodoList;
import com.todo.todoList.domain.model.User;
import com.todo.todoList.infrastructure.adapter.out.persistence.entity.TodoListJpaEntity;
import com.todo.todoList.infrastructure.adapter.out.persistence.entity.UserJpaEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper between TodoList domain model and TodoListJpaEntity
 */
@Component
public class TodoListMapper {

    private final TaskMapper taskMapper;

    public TodoListMapper(TaskMapper taskMapper) {
        this.taskMapper = taskMapper;
    }

    public TodoList toDomain(TodoListJpaEntity entity) {
        if (entity == null) return null;

        return TodoList.builder()
                .id(entity.getId())
                .name(entity.getName())
                .color(entity.getColor())
                .listType(entity.getListType())
                .tasks(entity.getTasks() != null ?
                        entity.getTasks().stream()
                                .map(taskMapper::toDomain)
                                .collect(Collectors.toList())
                        : null)
                .user(entity.getUser() != null ?
                        User.builder()
                                .id(entity.getUser().getId())
                                .email(entity.getUser().getEmail())
                                .nickname(entity.getUser().getNickname())
                                .build()
                        : null)
                .build();
    }

    public TodoListJpaEntity toJpaEntity(TodoList domain, UserJpaEntity userEntity) {
        if (domain == null) return null;

        return TodoListJpaEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .color(domain.getColor())
                .listType(domain.getListType())
                .user(userEntity)
                .build();
    }

    public void updateJpaEntityFromDomain(TodoList domain, TodoListJpaEntity entity) {
        entity.setName(domain.getName());
        entity.setColor(domain.getColor());
        entity.setListType(domain.getListType());
    }

    /**
     * Maps a list of JPA entities to domain models
     */
    public List<TodoList> toDomainList(List<TodoListJpaEntity> entities) {
        if (entities == null) return null;
        return entities.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    /**
     * Maps a domain model to JPA entity without relationships (for avoiding circular references)
     */
    public TodoList toDomainShallow(TodoListJpaEntity entity) {
        if (entity == null) return null;

        return TodoList.builder()
                .id(entity.getId())
                .name(entity.getName())
                .color(entity.getColor())
                .listType(entity.getListType())
                .build();
    }
}
