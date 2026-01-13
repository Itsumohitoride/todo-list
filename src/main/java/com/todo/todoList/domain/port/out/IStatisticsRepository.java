package com.todo.todoList.domain.port.out;

import com.todo.todoList.domain.model.Statistics;

import java.util.Optional;
import java.util.UUID;

/**
 * Port (Interface) for Statistics Repository - Outbound
 */
public interface IStatisticsRepository {
    Statistics save(Statistics statistics);
    Optional<Statistics> findById(UUID id);
    Optional<Statistics> findByUserId(UUID userId);
    boolean existsByUserId(UUID userId);
    void deleteByUserId(UUID userId);
}
