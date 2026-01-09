package com.todo.todoList.infrastructure.adapter.in.rest;

import com.todo.todoList.application.dto.UserDTO;
import com.todo.todoList.domain.model.User;
import com.todo.todoList.domain.port.in.IManageUserUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * REST Controller for User operations - Infrastructure Layer (Input Adapter)
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final IManageUserUseCase userUseCase;

    public UserController(IManageUserUseCase userUseCase) {
        this.userUseCase = userUseCase;
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO userDTO) {
        User user = toEntity(userDTO);
        User created = userUseCase.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable UUID id) {
        User user = userUseCase.getUserById(id);
        return ResponseEntity.ok(toDTO(user));
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<User> users = userUseCase.getAllUsers();
        List<UserDTO> userDTOs = users.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(userDTOs);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserDTO> getUserByEmail(@PathVariable String email) {
        User user = userUseCase.getUserByEmail(email);
        return ResponseEntity.ok(toDTO(user));
    }

    @GetMapping("/nickname/{nickname}")
    public ResponseEntity<UserDTO> getUserByNickname(@PathVariable String nickname) {
        User user = userUseCase.getUserByNickname(nickname);
        return ResponseEntity.ok(toDTO(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable UUID id, @Valid @RequestBody UserDTO userDTO) {
        User user = toEntity(userDTO);
        User updated = userUseCase.updateUser(id, user);
        return ResponseEntity.ok(toDTO(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userUseCase.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // ====================================
    // Private Helper Methods (DTO Mapping)
    // ====================================

    private UserDTO toDTO(User user) {
        if (user == null) return null;

        return UserDTO.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .build();
    }

    private User toEntity(UserDTO dto) {
        if (dto == null) return null;

        return User.builder()
                .id(dto.getId())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .nickname(dto.getNickname())
                .build();
    }
}
