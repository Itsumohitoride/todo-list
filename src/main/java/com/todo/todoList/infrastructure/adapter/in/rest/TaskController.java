package com.todo.todoList.infrastructure.adapter.in.rest;

import com.todo.todoList.application.dto.TaskDTO;
import com.todo.todoList.domain.enums.Status;
import com.todo.todoList.domain.model.Task;
import com.todo.todoList.domain.model.TodoList;
import com.todo.todoList.domain.port.in.IManageTaskUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * REST Controller for Task operations - Infrastructure Layer (Input Adapter)
 */
@RestController
@RequestMapping("/api")
@Tag(name = "Tasks", description = "Task management endpoints")
public class TaskController {

    private final IManageTaskUseCase taskUseCase;

    public TaskController(IManageTaskUseCase taskUseCase) {
        this.taskUseCase = taskUseCase;
    }

    @Operation(summary = "Create a new task",
               description = "Creates a new task in a specific todo list. The 'id', 'status', 'completed', and 'todoListId' fields are auto-generated/managed and cannot be provided in the request. The list ID comes from the URL path parameter.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Task created successfully",
                    content = @Content(schema = @Schema(implementation = TaskDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "TodoList not found")
    })
    @PostMapping("/lists/{listId}/tasks")
    public ResponseEntity<TaskDTO> createTask(@PathVariable UUID listId, @Valid @RequestBody TaskDTO taskDTO) {
        // Ignore todoListId from DTO, use listId from path parameter
        Task task = toEntityForCreate(taskDTO);
        Task created = taskUseCase.createTask(task, listId);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(created));
    }

    @Operation(summary = "Get task by ID", description = "Retrieves a task by its unique identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task found",
                    content = @Content(schema = @Schema(implementation = TaskDTO.class))),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @GetMapping("/tasks/{id}")
    public ResponseEntity<TaskDTO> getTaskById(@PathVariable UUID id) {
        Task task = taskUseCase.getTaskById(id)
                .orElseThrow(() -> new com.todo.todoList.domain.exception.EntityNotFoundException("Task", id));
        return ResponseEntity.ok(toDTO(task));
    }

    @Operation(summary = "Get tasks by todo list", description = "Retrieves all tasks from a specific todo list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "TodoList not found")
    })
    @GetMapping("/lists/{listId}/tasks")
    public ResponseEntity<List<TaskDTO>> getTasksByTodoList(@PathVariable UUID listId) {
        List<Task> tasks = taskUseCase.getTasksByTodoListId(listId);
        List<TaskDTO> taskDTOs = tasks.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(taskDTOs);
    }

    @Operation(summary = "Update task", description = "Updates an existing task's information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task updated successfully",
                    content = @Content(schema = @Schema(implementation = TaskDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @PutMapping("/tasks/{id}")
    public ResponseEntity<TaskDTO> updateTask(@PathVariable UUID id, @Valid @RequestBody TaskDTO taskDTO) {
        Task task = toEntity(taskDTO);
        Task updated = taskUseCase.updateTask(id, task);
        return ResponseEntity.ok(toDTO(updated));
    }

    @Operation(summary = "Mark task as completed", description = "Marks a task as completed and sets the completion date")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task marked as completed successfully",
                    content = @Content(schema = @Schema(implementation = TaskDTO.class))),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @PutMapping("/tasks/{id}/complete")
    public ResponseEntity<TaskDTO> markTaskAsCompleted(@PathVariable UUID id) {
        Task completed = taskUseCase.markAsCompleted(id);
        return ResponseEntity.ok(toDTO(completed));
    }

    @Operation(summary = "Mark task as pending", description = "Marks a task as pending and clears the completion date")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task marked as pending successfully",
                    content = @Content(schema = @Schema(implementation = TaskDTO.class))),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @PutMapping("/tasks/{id}/pending")
    public ResponseEntity<TaskDTO> markTaskAsPending(@PathVariable UUID id) {
        Task pending = taskUseCase.markAsPending(id);
        return ResponseEntity.ok(toDTO(pending));
    }

    @Operation(summary = "Delete task", description = "Deletes a task by its unique identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Task deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable UUID id) {
        taskUseCase.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    // ====================================
    // Search and Filter Endpoints
    // ====================================

    @Operation(summary = "Search tasks by description",
            description = "Searches all tasks by description (case-insensitive). Returns all tasks matching the search term.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search completed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid search term")
    })
    @GetMapping("/tasks/search")
    public ResponseEntity<List<TaskDTO>> searchTasks(
            @RequestParam(required = true) String description) {
        List<Task> results = taskUseCase.searchByDescription(description);
        return ResponseEntity.ok(results.stream()
                .map(this::toDTO)
                .collect(Collectors.toList()));
    }

    @Operation(summary = "Search tasks in a specific list by description",
            description = "Searches tasks in a specific todo list by description (case-insensitive)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search completed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid search parameters"),
            @ApiResponse(responseCode = "404", description = "TodoList not found")
    })
    @GetMapping("/lists/{listId}/tasks/search")
    public ResponseEntity<List<TaskDTO>> searchListTasks(
            @PathVariable UUID listId,
            @RequestParam(required = true) String description) {
        List<Task> results = taskUseCase.searchByTodoListIdAndDescription(listId, description);
        return ResponseEntity.ok(results.stream()
                .map(this::toDTO)
                .collect(Collectors.toList()));
    }

    @Operation(summary = "Filter tasks by status",
            description = "Filters tasks in a specific todo list by status (PENDING or COMPLETED)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Filter completed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid status or parameters"),
            @ApiResponse(responseCode = "404", description = "TodoList not found")
    })
    @GetMapping("/lists/{listId}/tasks/filter/status")
    public ResponseEntity<List<TaskDTO>> filterTasksByStatus(
            @PathVariable UUID listId,
            @RequestParam(required = true) Status status) {
        List<Task> results = taskUseCase.filterByTodoListIdAndStatus(listId, status);
        return ResponseEntity.ok(results.stream()
                .map(this::toDTO)
                .collect(Collectors.toList()));
    }

    @Operation(summary = "Filter tasks by date range",
            description = "Filters tasks in a specific todo list by date range. Dates should be in ISO-8601 format (e.g., 2024-01-01)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Filter completed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid date range or parameters"),
            @ApiResponse(responseCode = "404", description = "TodoList not found")
    })
    @GetMapping("/lists/{listId}/tasks/filter/date")
    public ResponseEntity<List<TaskDTO>> filterTasksByDateRange(
            @PathVariable UUID listId,
            @RequestParam(required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Task> results = taskUseCase.filterByTodoListIdAndDateRange(listId, startDate, endDate);
        return ResponseEntity.ok(results.stream()
                .map(this::toDTO)
                .collect(Collectors.toList()));
    }

    @Operation(summary = "Get overdue tasks",
            description = "Retrieves all overdue tasks (PENDING status with date in the past) from a specific todo list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Overdue tasks retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "TodoList not found")
    })
    @GetMapping("/lists/{listId}/tasks/overdue")
    public ResponseEntity<List<TaskDTO>> getOverdueTasks(@PathVariable UUID listId) {
        List<Task> results = taskUseCase.getOverdueTasks(listId);
        return ResponseEntity.ok(results.stream()
                .map(this::toDTO)
                .collect(Collectors.toList()));
    }

    // ====================================
    // Private Helper Methods (DTO Mapping)
    // ====================================

    private TaskDTO toDTO(Task task) {
        if (task == null) return null;

        return TaskDTO.builder()
                .id(task.getId())
                .description(task.getDescription())
                .status(task.getStatus())
                .type(task.getType())
                .date(task.getDate())
                .completed(task.getCompleted())
                .todoListId(task.getList() != null ? task.getList().getId() : null)
                .build();
    }

    private Task toEntity(TaskDTO dto) {
        if (dto == null) return null;

        TodoList todoList = null;
        if (dto.getTodoListId() != null) {
            todoList = TodoList.builder()
                    .id(dto.getTodoListId())
                    .build();
        }

        return Task.builder()
                .id(dto.getId())  // Will be null for POST requests due to @JsonProperty(access = READ_ONLY)
                .description(dto.getDescription())
                .status(dto.getStatus())
                .type(dto.getType())
                .date(dto.getDate())
                .completed(dto.getCompleted())
                .list(todoList)
                .build();
    }

    /**
     * Converts TaskDTO to Task entity for creation (ignores ID, status, completed, and todoListId from DTO)
     * The todoListId comes from the path parameter, not from the request body
     */
    private Task toEntityForCreate(TaskDTO dto) {
        if (dto == null) return null;

        return Task.builder()
                // ID, status, completed, and list will be set by the use case
                .description(dto.getDescription())
                .type(dto.getType())
                .date(dto.getDate())
                .build();
    }
}
