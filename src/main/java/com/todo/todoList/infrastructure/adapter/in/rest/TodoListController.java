package com.todo.todoList.infrastructure.adapter.in.rest;

import com.todo.todoList.application.dto.TodoListDTO;
import com.todo.todoList.domain.model.TodoList;
import com.todo.todoList.domain.model.User;
import com.todo.todoList.domain.port.in.IManageTodoListUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * REST Controller for TodoList operations - Infrastructure Layer (Input Adapter)
 */
@RestController
@RequestMapping("/api/lists")
@Tag(name = "TodoLists", description = "TodoList management endpoints")
public class TodoListController {

    private final IManageTodoListUseCase todoListUseCase;

    public TodoListController(IManageTodoListUseCase todoListUseCase) {
        this.todoListUseCase = todoListUseCase;
    }

    @Operation(summary = "Create a new todo list", description = "Creates a new todo list for a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "TodoList created successfully",
                    content = @Content(schema = @Schema(implementation = TodoListDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PostMapping
    public ResponseEntity<TodoListDTO> createTodoList(@Valid @RequestBody TodoListDTO todoListDTO) {
        TodoList todoList = toEntity(todoListDTO);
        TodoList created = todoListUseCase.createTodoList(todoList);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(created));
    }

    @Operation(summary = "Get todo list by ID", description = "Retrieves a todo list by its unique identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "TodoList found",
                    content = @Content(schema = @Schema(implementation = TodoListDTO.class))),
            @ApiResponse(responseCode = "404", description = "TodoList not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<TodoListDTO> getTodoListById(@PathVariable UUID id) {
        TodoList todoList = todoListUseCase.getTodoListById(id);
        return ResponseEntity.ok(toDTO(todoList));
    }

    @Operation(summary = "Get todo lists", description = "Retrieves all todo lists or filters by user ID if provided")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "TodoLists retrieved successfully")
    })
    @GetMapping
    public ResponseEntity<List<TodoListDTO>> getTodoLists(@RequestParam(required = false) UUID userId) {
        List<TodoList> todoLists;

        if (userId != null) {
            todoLists = todoListUseCase.getTodoListsByUserId(userId);
        } else {
            todoLists = todoListUseCase.getAllTodoLists();
        }

        List<TodoListDTO> todoListDTOs = todoLists.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(todoListDTOs);
    }

    @Operation(summary = "Update todo list", description = "Updates an existing todo list's information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "TodoList updated successfully",
                    content = @Content(schema = @Schema(implementation = TodoListDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "TodoList not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<TodoListDTO> updateTodoList(@PathVariable UUID id, @Valid @RequestBody TodoListDTO todoListDTO) {
        TodoList todoList = toEntity(todoListDTO);
        TodoList updated = todoListUseCase.updateTodoList(id, todoList);
        return ResponseEntity.ok(toDTO(updated));
    }

    @Operation(summary = "Change todo list color", description = "Updates the color of a todo list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Color changed successfully",
                    content = @Content(schema = @Schema(implementation = TodoListDTO.class))),
            @ApiResponse(responseCode = "404", description = "TodoList not found")
    })
    @PutMapping("/{id}/color")
    public ResponseEntity<TodoListDTO> changeColor(@PathVariable UUID id, @RequestBody ColorRequest colorRequest) {
        TodoList updated = todoListUseCase.changeColor(id, colorRequest.getColor());
        return ResponseEntity.ok(toDTO(updated));
    }

    @Operation(summary = "Delete todo list", description = "Deletes a todo list by its unique identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "TodoList deleted successfully"),
            @ApiResponse(responseCode = "404", description = "TodoList not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodoList(@PathVariable UUID id) {
        todoListUseCase.deleteTodoList(id);
        return ResponseEntity.noContent().build();
    }

    // ====================================
    // Private Helper Methods (DTO Mapping)
    // ====================================

    private TodoListDTO toDTO(TodoList todoList) {
        if (todoList == null) return null;

        return TodoListDTO.builder()
                .id(todoList.getId())
                .name(todoList.getName())
                .color(todoList.getColor())
                .listType(todoList.getListType())
                .userId(todoList.getUser() != null ? todoList.getUser().getId() : null)
                .build();
    }

    private TodoList toEntity(TodoListDTO dto) {
        if (dto == null) return null;

        User user = null;
        if (dto.getUserId() != null) {
            user = User.builder()
                    .id(dto.getUserId())
                    .build();
        }

        return TodoList.builder()
                .id(dto.getId())
                .name(dto.getName())
                .color(dto.getColor())
                .listType(dto.getListType())
                .user(user)
                .build();
    }

    // Inner class for color change request
    @Getter
    @Setter
    @Schema(description = "Request body for changing todo list color")
    public static class ColorRequest {
        @Schema(description = "Hex color code", example = "#FF5733")
        private String color;
    }
}
