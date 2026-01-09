package com.todo.todoList.application.usecase;

import com.todo.todoList.domain.exception.DuplicateEntityException;
import com.todo.todoList.domain.exception.EntityNotFoundException;
import com.todo.todoList.domain.model.User;
import com.todo.todoList.domain.port.out.IUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserUseCase
 * Uses mocks to test use case logic in isolation
 */
@ExtendWith(MockitoExtension.class)
class UserUseCaseTest {

    @Mock
    private IUserRepository userRepository;

    @InjectMocks
    private UserUseCase userUseCase;

    private User testUser;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        testUser = User.builder()
                .id(userId)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .nickname("johndoe")
                .lists(new ArrayList<>())
                .build();
    }

    @Test
    void testCreateUser_WithValidUser_ShouldReturnCreatedUser() {
        // Given
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.empty());
        when(userRepository.findByNickname(testUser.getNickname())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        User result = userUseCase.createUser(testUser);

        // Then
        assertNotNull(result);
        assertEquals(testUser.getEmail(), result.getEmail());
        assertEquals(testUser.getNickname(), result.getNickname());
        verify(userRepository).findByEmail(testUser.getEmail());
        verify(userRepository).findByNickname(testUser.getNickname());
        verify(userRepository).save(testUser);
    }

    @Test
    void testCreateUser_WithDuplicateEmail_ShouldThrowException() {
        // Given
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));

        // When & Then
        assertThrows(DuplicateEntityException.class, () -> userUseCase.createUser(testUser));
        verify(userRepository).findByEmail(testUser.getEmail());
        verify(userRepository, never()).save(any());
    }

    @Test
    void testCreateUser_WithDuplicateNickname_ShouldThrowException() {
        // Given
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.empty());
        when(userRepository.findByNickname(testUser.getNickname())).thenReturn(Optional.of(testUser));

        // When & Then
        assertThrows(DuplicateEntityException.class, () -> userUseCase.createUser(testUser));
        verify(userRepository).findByEmail(testUser.getEmail());
        verify(userRepository).findByNickname(testUser.getNickname());
        verify(userRepository, never()).save(any());
    }

    @Test
    void testUpdateUser_WithValidUser_ShouldReturnUpdatedUser() {
        // Given
        User updatedUser = User.builder()
                .id(userId)
                .firstName("Jane")
                .lastName("Smith")
                .email(testUser.getEmail())
                .nickname(testUser.getNickname())
                .lists(new ArrayList<>())
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        // When
        User result = userUseCase.updateUser(userId, updatedUser);

        // Then
        assertNotNull(result);
        assertEquals("Jane", result.getFirstName());
        assertEquals("Smith", result.getLastName());
        verify(userRepository).findById(userId);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testUpdateUser_WithNonExistentUser_ShouldThrowException() {
        // Given
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> userUseCase.updateUser(userId, testUser));
        verify(userRepository).findById(userId);
        verify(userRepository, never()).save(any());
    }

    @Test
    void testGetUserById_WithExistingUser_ShouldReturnUser() {
        // Given
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        // When
        User result = userUseCase.getUserById(userId);

        // Then
        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals(testUser.getEmail(), result.getEmail());
        verify(userRepository).findById(userId);
    }

    @Test
    void testGetUserById_WithNonExistentUser_ShouldThrowException() {
        // Given
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> userUseCase.getUserById(userId));
        verify(userRepository).findById(userId);
    }

    @Test
    void testGetAllUsers_ShouldReturnAllUsers() {
        // Given
        User user2 = User.builder()
                .id(UUID.randomUUID())
                .firstName("Jane")
                .lastName("Smith")
                .email("jane@example.com")
                .nickname("janesmith")
                .lists(new ArrayList<>())
                .build();

        List<User> users = Arrays.asList(testUser, user2);
        when(userRepository.findAll()).thenReturn(users);

        // When
        List<User> result = userUseCase.getAllUsers();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userRepository).findAll();
    }

    @Test
    void testDeleteUser_WithExistingUser_ShouldDeleteUser() {
        // Given
        when(userRepository.existsById(userId)).thenReturn(true);
        doNothing().when(userRepository).deleteById(userId);

        // When
        userUseCase.deleteUser(userId);

        // Then
        verify(userRepository).existsById(userId);
        verify(userRepository).deleteById(userId);
    }

    @Test
    void testDeleteUser_WithNonExistentUser_ShouldThrowException() {
        // Given
        when(userRepository.existsById(userId)).thenReturn(false);

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> userUseCase.deleteUser(userId));
        verify(userRepository).existsById(userId);
        verify(userRepository, never()).deleteById(any());
    }
}
