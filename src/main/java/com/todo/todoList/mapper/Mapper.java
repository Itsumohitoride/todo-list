package com.todo.todoList.mapper;

import com.todo.todoList.dto.UserDTO;
import com.todo.todoList.model.User;

public class Mapper {
    public static User toObject(UserDTO userDTO) {
        return User.builder()
                .firstName(userDTO.getFirstName())
                .lastName(userDTO.getFirstName())
                .email(userDTO.getEmail())
                .nickname(userDTO.getNickname())
                .build();
    }

    public static UserDTO toDTO(User user) {
        return UserDTO.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .build();
    }
}
