package com.todo.todoList.application.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.todo.todoList.domain.enums.UniqueType;
import com.todo.todoList.infrastructure.validation.annotation.UniqueField;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "User Data Transfer Object")
public class UserDTO{
    private UUID id;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @UniqueField(type = UniqueType.EMAIL, message = "Email already exists")
    private String email;

    @NotBlank(message = "Nickname is required")
    @UniqueField(type = UniqueType.NICKNAME, message = "Nickname already exists")
    private String nickname;
}
