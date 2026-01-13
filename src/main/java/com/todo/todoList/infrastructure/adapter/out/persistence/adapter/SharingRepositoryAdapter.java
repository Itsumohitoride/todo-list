package com.todo.todoList.infrastructure.adapter.out.persistence.adapter;

import com.todo.todoList.domain.model.Sharing;
import com.todo.todoList.domain.port.out.ISharingRepository;
import com.todo.todoList.infrastructure.adapter.out.persistence.entity.SharingJpaEntity;
import com.todo.todoList.infrastructure.adapter.out.persistence.entity.TodoListJpaEntity;
import com.todo.todoList.infrastructure.adapter.out.persistence.entity.UserJpaEntity;
import com.todo.todoList.infrastructure.adapter.out.persistence.repository.SharingJpaRepository;
import com.todo.todoList.infrastructure.adapter.out.persistence.repository.TodoListJpaRepository;
import com.todo.todoList.infrastructure.adapter.out.persistence.repository.UserJpaRepository;
import com.todo.todoList.infrastructure.mapper.SharingMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Adapter implementing ISharingRepository port - Infrastructure Layer
 */
@Component
public class SharingRepositoryAdapter implements ISharingRepository {

    private final SharingJpaRepository jpaRepository;
    private final TodoListJpaRepository todoListJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final SharingMapper mapper;

    public SharingRepositoryAdapter(SharingJpaRepository jpaRepository,
                                    TodoListJpaRepository todoListJpaRepository,
                                    UserJpaRepository userJpaRepository,
                                    SharingMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.todoListJpaRepository = todoListJpaRepository;
        this.userJpaRepository = userJpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Sharing save(Sharing sharing) {
        TodoListJpaEntity listEntity = null;

        if (sharing.getList() != null && sharing.getList().getId() != null) {
            listEntity = todoListJpaRepository.findById(sharing.getList().getId())
                    .orElseThrow(() -> new RuntimeException("TodoList not found: " + sharing.getList().getId()));
        }

        SharingJpaEntity entity;

        if (sharing.getId() != null && jpaRepository.existsById(sharing.getId())) {
            // Update existing
            entity = jpaRepository.findById(sharing.getId()).orElseThrow();

            // Update users list
            if (sharing.getUsers() != null) {
                List<UserJpaEntity> userEntities = sharing.getUsers().stream()
                        .map(user -> userJpaRepository.findById(user.getId())
                                .orElseThrow(() -> new RuntimeException("User not found: " + user.getId())))
                        .collect(Collectors.toList());
                entity.setUsers(userEntities);
            }
        } else {
            // Create new
            entity = mapper.toJpaEntity(sharing, listEntity);
        }

        SharingJpaEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Sharing> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Sharing> findByTodoListId(UUID todoListId) {
        return jpaRepository.findByListId(todoListId)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Sharing> findByShareToken(String shareToken) {
        return jpaRepository.findByShareToken(shareToken)
                .map(mapper::toDomain);
    }

    @Override
    public List<Sharing> findAll() {
        return jpaRepository.findAll().stream()
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

    @Override
    public boolean existsByShareToken(String shareToken) {
        return jpaRepository.existsByShareToken(shareToken);
    }
}
