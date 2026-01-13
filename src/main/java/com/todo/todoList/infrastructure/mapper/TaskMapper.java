package com.todo.todoList.infrastructure.mapper;

import com.todo.todoList.domain.model.Task;
import com.todo.todoList.domain.model.TodoList;
import com.todo.todoList.infrastructure.adapter.out.persistence.entity.TaskJpaEntity;
import com.todo.todoList.infrastructure.adapter.out.persistence.entity.TodoListJpaEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper between Task domain model and TaskJpaEntity
 */
@Component
public class TaskMapper {

    public Task toDomain(TaskJpaEntity entity) {
        if (entity == null) return null;

        return Task.builder()
                .id(entity.getId())
                .description(entity.getDescription())
                .status(entity.getStatus())
                .type(entity.getType())
                .date(entity.getDate())
                .completed(entity.getCompleted())
                .list(entity.getList() != null ?
                        TodoList.builder()
                                .id(entity.getList().getId())
                                .name(entity.getList().getName())
                                .color(entity.getList().getColor())
                                .listType(entity.getList().getListType())
                                .build()
                        : null)
                .build();
    }

    public TaskJpaEntity toJpaEntity(Task domain, TodoListJpaEntity listEntity) {
        if (domain == null) return null;

        return TaskJpaEntity.builder()
                .id(domain.getId())
                .description(domain.getDescription())
                .status(domain.getStatus())
                .type(domain.getType())
                .date(domain.getDate())
                .completed(domain.getCompleted())
                .list(listEntity)
                .build();
    }

    public void updateJpaEntityFromDomain(Task domain, TaskJpaEntity entity) {
        entity.setDescription(domain.getDescription());
        entity.setStatus(domain.getStatus());
        entity.setType(domain.getType());
        entity.setDate(domain.getDate());
        entity.setCompleted(domain.getCompleted());
    }

    /**
     * Maps a list of JPA entities to domain models
     */
    public List<Task> toDomainList(List<TaskJpaEntity> entities) {
        if (entities == null) return null;
        return entities.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    /**
     * Maps a domain model to JPA entity without relationships (for avoiding circular references)
     */
    public Task toDomainShallow(TaskJpaEntity entity) {
        if (entity == null) return null;

        return Task.builder()
                .id(entity.getId())
                .description(entity.getDescription())
                .status(entity.getStatus())
                .type(entity.getType())
                .date(entity.getDate())
                .completed(entity.getCompleted())
                .build();
    }
}
