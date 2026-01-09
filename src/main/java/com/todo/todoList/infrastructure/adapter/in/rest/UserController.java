package com.todo.todoList.infrastructure.adapter.in.rest;

import com.todo.todoList.application.dto.UserDTO;
import com.todo.todoList.domain.model.User;
import com.todo.todoList.domain.port.in.IManageUserUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Users", description = "User management endpoints")
public class UserController {

    private final IManageUserUseCase userUseCase;

    public UserController(IManageUserUseCase userUseCase) {
        this.userUseCase = userUseCase;
    }

    @Operation(summary = "Create a new user",
               description = "Creates a new user with the provided information. The 'id' field is auto-generated and cannot be provided in the request.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully",
                    content = @Content(schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "409", description = "User already exists (duplicate email or nickname)")
    })
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO userDTO) {
        User user = toEntity(userDTO);
        User created = userUseCase.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(created));
    }

    @Operation(summary = "Get user by ID", description = "Retrieves a user by their unique identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found",
                    content = @Content(schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable UUID id) {
        User user = userUseCase.getUserById(id);
        return ResponseEntity.ok(toDTO(user));
    }

    @Operation(summary = "Get all users", description = "Retrieves a list of all users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully")
    })
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<User> users = userUseCase.getAllUsers();
        List<UserDTO> userDTOs = users.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(userDTOs);
    }

    @Operation(summary = "Get user by email", description = "Retrieves a user by their email address")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found",
                    content = @Content(schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/email/{email}")
    public ResponseEntity<UserDTO> getUserByEmail(@PathVariable String email) {
        User user = userUseCase.getUserByEmail(email);
        return ResponseEntity.ok(toDTO(user));
    }

    @Operation(summary = "Get user by nickname", description = "Retrieves a user by their nickname")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found",
                    content = @Content(schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/nickname/{nickname}")
    public ResponseEntity<UserDTO> getUserByNickname(@PathVariable String nickname) {
        User user = userUseCase.getUserByNickname(nickname);
        return ResponseEntity.ok(toDTO(user));
    }

    @Operation(summary = "Update user", description = "Updates an existing user's information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully",
                    content = @Content(schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "409", description = "Duplicate email or nickname")
    })
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable UUID id, @Valid @RequestBody UserDTO userDTO) {
        User user = toEntity(userDTO);
        User updated = userUseCase.updateUser(id, user);
        return ResponseEntity.ok(toDTO(updated));
    }

    @Operation(summary = "Delete user", description = "Deletes a user by their unique identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
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
                .id(dto.getId())  // Will be null for POST requests due to @JsonProperty(access = READ_ONLY)
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .nickname(dto.getNickname())
                .build();
    }
}
