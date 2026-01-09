package com.todo.todoList.application.usecase;

import com.todo.todoList.domain.exception.DuplicateEntityException;
import com.todo.todoList.domain.exception.EntityNotFoundException;
import com.todo.todoList.domain.model.User;
import com.todo.todoList.domain.port.in.IManageUserUseCase;
import com.todo.todoList.domain.port.out.IUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * User Use Case Implementation - Application Layer
 */
@Service
@Transactional
public class UserUseCase implements IManageUserUseCase {

    private final IUserRepository userRepository;

    public UserUseCase(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User createUser(User user) {
        // Validate email uniqueness
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new DuplicateEntityException("Email", user.getEmail());
        }

        // Validate nickname uniqueness
        if (userRepository.findByNickname(user.getNickname()).isPresent()) {
            throw new DuplicateEntityException("Nickname", user.getNickname());
        }

        return userRepository.save(user);
    }

    @Override
    public User updateUser(UUID id, User user) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User", id));

        // Validate email uniqueness (if changed)
        if (!existingUser.getEmail().equals(user.getEmail())) {
            if (userRepository.findByEmail(user.getEmail()).isPresent()) {
                throw new DuplicateEntityException("Email", user.getEmail());
            }
        }

        // Validate nickname uniqueness (if changed)
        if (!existingUser.getNickname().equals(user.getNickname())) {
            if (userRepository.findByNickname(user.getNickname()).isPresent()) {
                throw new DuplicateEntityException("Nickname", user.getNickname());
            }
        }

        // Update fields
        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setEmail(user.getEmail());
        existingUser.setNickname(user.getNickname());
        existingUser.setProfilePicture(user.getProfilePicture());

        return userRepository.save(existingUser);
    }

    @Override
    public void deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User", id);
        }
        userRepository.deleteById(id);
    }

    @Override
    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User", id));
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User with email " + email + " not found"));
    }

    @Override
    public User getUserByNickname(String nickname) {
        return userRepository.findByNickname(nickname)
                .orElseThrow(() -> new EntityNotFoundException("User with nickname " + nickname + " not found"));
    }
}
