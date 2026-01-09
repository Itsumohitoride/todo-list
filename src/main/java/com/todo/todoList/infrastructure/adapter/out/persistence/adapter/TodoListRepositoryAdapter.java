package com.todo.todoList.infrastructure.adapter.out.persistence.adapter;

import com.todo.todoList.domain.model.TodoList;
import com.todo.todoList.domain.port.out.ITodoListRepository;
import com.todo.todoList.infrastructure.adapter.out.persistence.entity.TodoListJpaEntity;
import com.todo.todoList.infrastructure.adapter.out.persistence.entity.UserJpaEntity;
import com.todo.todoList.infrastructure.adapter.out.persistence.repository.TodoListJpaRepository;
import com.todo.todoList.infrastructure.adapter.out.persistence.repository.UserJpaRepository;
import com.todo.todoList.infrastructure.mapper.TodoListMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Adapter implementing ITodoListRepository port - Infrastructure Layer
 */
@Component
public class TodoListRepositoryAdapter implements ITodoListRepository {

    private final TodoListJpaRepository jpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final TodoListMapper mapper;

    public TodoListRepositoryAdapter(TodoListJpaRepository jpaRepository,
                                     UserJpaRepository userJpaRepository,
                                     TodoListMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.userJpaRepository = userJpaRepository;
        this.mapper = mapper;
    }

    @Override
    public TodoList save(TodoList todoList) {
        UserJpaEntity userEntity = null;

        if (todoList.getUser() != null && todoList.getUser().getId() != null) {
            userEntity = userJpaRepository.findById(todoList.getUser().getId())
                    .orElseThrow(() -> new RuntimeException("User not found: " + todoList.getUser().getId()));
        }

        TodoListJpaEntity entity;

        if (todoList.getId() != null && jpaRepository.existsById(todoList.getId())) {
            // Update existing
            entity = jpaRepository.findById(todoList.getId()).orElseThrow();
            mapper.updateJpaEntityFromDomain(todoList, entity);
        } else {
            // Create new
            entity = mapper.toJpaEntity(todoList, userEntity);
        }

        TodoListJpaEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<TodoList> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<TodoList> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<TodoList> findByUserId(UUID userId) {
        return jpaRepository.findByUserId(userId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return jpaRepository.existsById(id);
    }
}
