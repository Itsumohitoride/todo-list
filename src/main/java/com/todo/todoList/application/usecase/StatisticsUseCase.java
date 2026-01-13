package com.todo.todoList.application.usecase;

import com.todo.todoList.application.dto.ChartDataDTO;
import com.todo.todoList.application.dto.StatisticsDTO;
import com.todo.todoList.domain.enums.Status;
import com.todo.todoList.domain.exception.EntityNotFoundException;
import com.todo.todoList.domain.model.Statistics;
import com.todo.todoList.domain.model.Task;
import com.todo.todoList.domain.model.TodoList;
import com.todo.todoList.domain.port.in.IManageStatisticsUseCase;
import com.todo.todoList.domain.port.out.ITaskRepository;
import com.todo.todoList.domain.port.out.ITodoListRepository;
import com.todo.todoList.domain.port.out.IUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Statistics Use Case Implementation - Application Layer
 * Calculates statistics in real-time by querying repositories
 */
@Service
@Transactional(readOnly = true)
public class StatisticsUseCase implements IManageStatisticsUseCase {

    private final IUserRepository userRepository;
    private final ITodoListRepository todoListRepository;
    private final ITaskRepository taskRepository;

    public StatisticsUseCase(IUserRepository userRepository,
                           ITodoListRepository todoListRepository,
                           ITaskRepository taskRepository) {
        this.userRepository = userRepository;
        this.todoListRepository = todoListRepository;
        this.taskRepository = taskRepository;
    }

    @Override
    public StatisticsDTO calculateStatistics(UUID userId) {
        // Verify user exists
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User", userId);
        }

        // Get all lists for the user
        List<TodoList> userLists = todoListRepository.findByUserId(userId);
        int totalLists = userLists.size();

        // Get all tasks for all user's lists
        int totalTasks = 0;
        int completedTasks = 0;
        int pendingTasks = 0;

        for (TodoList todoList : userLists) {
            List<Task> tasks = taskRepository.findByTodoListId(todoList.getId());
            totalTasks += tasks.size();

            for (Task task : tasks) {
                if (Status.COMPLETED.equals(task.getStatus())) {
                    completedTasks++;
                } else {
                    pendingTasks++;
                }
            }
        }

        // Create Statistics object
        Statistics statistics = Statistics.builder()
                .id(UUID.randomUUID())
                .totalLists(totalLists)
                .totalTasks(totalTasks)
                .completedTasks(completedTasks)
                .pendingTasks(pendingTasks)
                .calculatedAt(LocalDateTime.now())
                .build();

        // Convert to DTO
        return StatisticsDTO.builder()
                .id(statistics.getId())
                .totalLists(statistics.getTotalLists())
                .totalTasks(statistics.getTotalTasks())
                .completedTasks(statistics.getCompletedTasks())
                .pendingTasks(statistics.getPendingTasks())
                .completionPercentage(statistics.getCompletionPercentage())
                .calculatedAt(statistics.getCalculatedAt())
                .userId(userId)
                .build();
    }

    @Override
    public ChartDataDTO generateProgressChart(UUID userId) {
        // Verify user exists
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User", userId);
        }

        // Get statistics
        StatisticsDTO stats = calculateStatistics(userId);

        // Create simple progress chart with current stats
        // In a more complex implementation, this would show historical data
        List<String> labels = Arrays.asList("Total Tasks", "Completed", "Pending");
        List<Integer> data = Arrays.asList(
                stats.getTotalTasks(),
                stats.getCompletedTasks(),
                stats.getPendingTasks()
        );

        ChartDataDTO.DatasetDTO dataset = ChartDataDTO.DatasetDTO.builder()
                .label("Task Progress")
                .data(data)
                .backgroundColor("#4CAF50")
                .borderColor("#45A049")
                .borderWidth(2)
                .build();

        return ChartDataDTO.builder()
                .labels(labels)
                .datasets(List.of(dataset))
                .build();
    }

    @Override
    public ChartDataDTO generateTasksBarChart(UUID userId) {
        // Verify user exists
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User", userId);
        }

        // Get statistics
        StatisticsDTO stats = calculateStatistics(userId);

        // Create bar chart with completed vs pending tasks
        List<String> labels = Arrays.asList("Completed", "Pending");
        List<Integer> completedData = Arrays.asList(stats.getCompletedTasks(), 0);
        List<Integer> pendingData = Arrays.asList(0, stats.getPendingTasks());

        ChartDataDTO.DatasetDTO completedDataset = ChartDataDTO.DatasetDTO.builder()
                .label("Completed Tasks")
                .data(completedData)
                .backgroundColor("#4CAF50")
                .borderColor("#45A049")
                .borderWidth(1)
                .build();

        ChartDataDTO.DatasetDTO pendingDataset = ChartDataDTO.DatasetDTO.builder()
                .label("Pending Tasks")
                .data(pendingData)
                .backgroundColor("#FF9800")
                .borderColor("#F57C00")
                .borderWidth(1)
                .build();

        return ChartDataDTO.builder()
                .labels(labels)
                .datasets(Arrays.asList(completedDataset, pendingDataset))
                .build();
    }
}
