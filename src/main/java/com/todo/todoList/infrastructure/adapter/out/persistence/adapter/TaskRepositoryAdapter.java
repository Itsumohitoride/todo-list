package com.todo.todoList.infrastructure.adapter.out.persistence.adapter;

import com.todo.todoList.domain.enums.Status;
import com.todo.todoList.domain.enums.TaskType;
import com.todo.todoList.domain.model.Task;
import com.todo.todoList.domain.port.out.ITaskRepository;
import com.todo.todoList.infrastructure.adapter.out.persistence.entity.TaskJpaEntity;
import com.todo.todoList.infrastructure.adapter.out.persistence.entity.TodoListJpaEntity;
import com.todo.todoList.infrastructure.adapter.out.persistence.repository.TaskJpaRepository;
import com.todo.todoList.infrastructure.adapter.out.persistence.repository.TodoListJpaRepository;
import com.todo.todoList.infrastructure.mapper.TaskMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Adapter implementing ITaskRepository port - Infrastructure Layer
 */
@Component
public class TaskRepositoryAdapter implements ITaskRepository {

    private final TaskJpaRepository jpaRepository;
    private final TodoListJpaRepository todoListJpaRepository;
    private final TaskMapper mapper;

    public TaskRepositoryAdapter(TaskJpaRepository jpaRepository,
                                 TodoListJpaRepository todoListJpaRepository,
                                 TaskMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.todoListJpaRepository = todoListJpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Task save(Task task) {
        TodoListJpaEntity listEntity = null;

        if (task.getList() != null && task.getList().getId() != null) {
            listEntity = todoListJpaRepository.findById(task.getList().getId())
                    .orElseThrow(() -> new RuntimeException("TodoList not found: " + task.getList().getId()));
        }

        TaskJpaEntity entity;

        if (task.getId() != null && jpaRepository.existsById(task.getId())) {
            // Update existing
            entity = jpaRepository.findById(task.getId()).orElseThrow();
            mapper.updateJpaEntityFromDomain(task, entity);
        } else {
            // Create new
            entity = mapper.toJpaEntity(task, listEntity);
        }

        TaskJpaEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Task> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<Task> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> findByTodoListId(UUID todoListId) {
        return jpaRepository.findByListId(todoListId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> findByType(TaskType type) {
        return jpaRepository.findByType(type).stream()
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
    public List<Task> searchByDescriptionContaining(String searchTerm) {
        return jpaRepository.findByDescriptionContainingIgnoreCase(searchTerm).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> searchByTodoListIdAndDescriptionContaining(UUID todoListId, String searchTerm) {
        return jpaRepository.findByListIdAndDescriptionContaining(todoListId, searchTerm).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> findByTodoListIdAndStatus(UUID todoListId, Status status) {
        return jpaRepository.findByListIdAndStatus(todoListId, status).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> findByTodoListIdAndDateBetween(UUID todoListId, LocalDate startDate, LocalDate endDate) {
        return jpaRepository.findByListIdAndDateBetween(todoListId, startDate, endDate).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> findOverdueTasks(UUID todoListId) {
        return jpaRepository.findOverdueTasksByListId(todoListId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
