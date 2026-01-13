package com.todo.todoList.infrastructure.adapter.out.persistence.adapter;

import com.todo.todoList.domain.model.User;
import com.todo.todoList.domain.port.out.IUserRepository;
import com.todo.todoList.infrastructure.adapter.out.persistence.entity.UserJpaEntity;
import com.todo.todoList.infrastructure.adapter.out.persistence.repository.UserJpaRepository;
import com.todo.todoList.infrastructure.mapper.UserMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Adapter implementing IUserRepository port - Infrastructure Layer
 */
@Component
public class UserRepositoryAdapter implements IUserRepository {

    private final UserJpaRepository jpaRepository;
    private final UserMapper mapper;

    public UserRepositoryAdapter(UserJpaRepository jpaRepository, UserMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public User save(User user) {
        UserJpaEntity entity;

        if (user.getId() != null && jpaRepository.existsById(user.getId())) {
            // Update existing
            entity = jpaRepository.findById(user.getId()).orElseThrow();
            mapper.updateJpaEntityFromDomain(user, entity);
        } else {
            // Create new
            entity = mapper.toJpaEntity(user);
        }

        UserJpaEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<User> findAll() {
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
    public Optional<User> findByEmail(String email) {
        return jpaRepository.findByEmail(email)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByNickname(String nickname) {
        return jpaRepository.findByNickname(nickname)
                .map(mapper::toDomain);
    }
}
