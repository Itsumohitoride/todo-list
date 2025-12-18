package com.todo.todoList.service;

import com.todo.todoList.dto.UserDTO;
import com.todo.todoList.mapper.Mapper;
import com.todo.todoList.model.User;
import com.todo.todoList.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService implements IUserService {
    private final UserRepository userRepository;

    @Override
    public UserDTO createUser(UserDTO userDTO) {
        User user = Mapper.toObject(userDTO);
        return Mapper.toDTO(userRepository.save(user));
    }

    @Override
    public UserDTO updateUser(UserDTO userDTO) {
        return null;
    }

    @Override
    public UserDTO deleteUser(UserDTO userDTO) {
        return null;
    }
}
