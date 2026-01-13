package com.todo.todoList.domain.port.in;

import com.todo.todoList.application.dto.ChartDataDTO;
import com.todo.todoList.application.dto.StatisticsDTO;

import java.util.UUID;

/**
 * Port (Interface) for Statistics Use Cases - Inbound
 */
public interface IManageStatisticsUseCase {
    /**
     * Calculate statistics for a specific user
     * @param userId User ID
     * @return Statistics DTO with calculated values
     */
    StatisticsDTO calculateStatistics(UUID userId);

    /**
     * Generate progress chart data for a user (line chart)
     * Shows task completion over time
     * @param userId User ID
     * @return Chart data in Chart.js format
     */
    ChartDataDTO generateProgressChart(UUID userId);

    /**
     * Generate tasks bar chart data for a user
     * Shows completed vs pending tasks
     * @param userId User ID
     * @return Chart data in Chart.js format
     */
    ChartDataDTO generateTasksBarChart(UUID userId);
}
