package com.todo.todoList.infrastructure.adapter.in.rest;

import com.todo.todoList.application.dto.TodoListDTO;
import com.todo.todoList.domain.model.TodoList;
import com.todo.todoList.domain.model.User;
import com.todo.todoList.domain.port.in.IManageTodoListUseCase;
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
public class TodoListController {

    private final IManageTodoListUseCase todoListUseCase;

    public TodoListController(IManageTodoListUseCase todoListUseCase) {
        this.todoListUseCase = todoListUseCase;
    }

    @PostMapping
    public ResponseEntity<TodoListDTO> createTodoList(@Valid @RequestBody TodoListDTO todoListDTO) {
        TodoList todoList = toEntity(todoListDTO);
        TodoList created = todoListUseCase.createTodoList(todoList);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TodoListDTO> getTodoListById(@PathVariable UUID id) {
        TodoList todoList = todoListUseCase.getTodoListById(id);
        return ResponseEntity.ok(toDTO(todoList));
    }

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

    @PutMapping("/{id}")
    public ResponseEntity<TodoListDTO> updateTodoList(@PathVariable UUID id, @Valid @RequestBody TodoListDTO todoListDTO) {
        TodoList todoList = toEntity(todoListDTO);
        TodoList updated = todoListUseCase.updateTodoList(id, todoList);
        return ResponseEntity.ok(toDTO(updated));
    }

    @PutMapping("/{id}/color")
    public ResponseEntity<TodoListDTO> changeColor(@PathVariable UUID id, @RequestBody ColorRequest colorRequest) {
        TodoList updated = todoListUseCase.changeColor(id, colorRequest.getColor());
        return ResponseEntity.ok(toDTO(updated));
    }

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
    public static class ColorRequest {
        private String color;
    }
}
