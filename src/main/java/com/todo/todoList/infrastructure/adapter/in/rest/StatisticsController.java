package com.todo.todoList.infrastructure.adapter.in.rest;

import com.todo.todoList.application.dto.ChartDataDTO;
import com.todo.todoList.application.dto.StatisticsDTO;
import com.todo.todoList.domain.port.in.IManageStatisticsUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST Controller for Statistics operations - Infrastructure Layer (Input Adapter)
 */
@RestController
@RequestMapping("/api/statistics")
@Tag(name = "Statistics", description = "Statistics and charts endpoints")
public class StatisticsController {

    private final IManageStatisticsUseCase statisticsUseCase;

    public StatisticsController(IManageStatisticsUseCase statisticsUseCase) {
        this.statisticsUseCase = statisticsUseCase;
    }

    @Operation(summary = "Get user statistics",
               description = "Calculates and retrieves statistics for a specific user including total lists, tasks, completion percentage, etc. Statistics are calculated in real-time.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully",
                    content = @Content(schema = @Schema(implementation = StatisticsDTO.class))),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/users/{userId}")
    public ResponseEntity<StatisticsDTO> getUserStatistics(@PathVariable UUID userId) {
        StatisticsDTO statistics = statisticsUseCase.calculateStatistics(userId);
        return ResponseEntity.ok(statistics);
    }

    @Operation(summary = "Get progress chart data",
               description = "Generates progress chart data for visualizing task completion. Returns data in Chart.js compatible format for line/bar charts.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Chart data generated successfully",
                    content = @Content(schema = @Schema(implementation = ChartDataDTO.class))),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/users/{userId}/progress")
    public ResponseEntity<ChartDataDTO> getProgressChart(@PathVariable UUID userId) {
        ChartDataDTO chartData = statisticsUseCase.generateProgressChart(userId);
        return ResponseEntity.ok(chartData);
    }

    @Operation(summary = "Get tasks bar chart data",
               description = "Generates bar chart data showing completed vs pending tasks. Returns data in Chart.js compatible format.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Chart data generated successfully",
                    content = @Content(schema = @Schema(implementation = ChartDataDTO.class))),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/users/{userId}/tasks")
    public ResponseEntity<ChartDataDTO> getTasksBarChart(@PathVariable UUID userId) {
        ChartDataDTO chartData = statisticsUseCase.generateTasksBarChart(userId);
        return ResponseEntity.ok(chartData);
    }
}
