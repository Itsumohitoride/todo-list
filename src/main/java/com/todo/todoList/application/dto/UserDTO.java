package com.todo.todoList.application.dto;

import com.todo.todoList.domain.enums.UniqueType;
import com.todo.todoList.infrastructure.validation.annotation.UniqueField;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserDTO{
    private String firstName;
    private String lastName;
    @UniqueField(type = UniqueType.EMAIL, message = "Email already exists")
    private String email;
    @UniqueField(type = UniqueType.NICKNAME, message = "Nickname already exists")
    private String nickname;
}
