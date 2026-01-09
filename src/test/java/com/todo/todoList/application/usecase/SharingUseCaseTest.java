package com.todo.todoList.application.usecase;

import com.todo.todoList.domain.exception.EntityNotFoundException;
import com.todo.todoList.domain.model.Sharing;
import com.todo.todoList.domain.model.TodoList;
import com.todo.todoList.domain.model.User;
import com.todo.todoList.domain.port.out.ISharingRepository;
import com.todo.todoList.domain.port.out.ITodoListRepository;
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
 * Unit tests for SharingUseCase
 * Uses mocks to test use case logic in isolation
 */
@ExtendWith(MockitoExtension.class)
class SharingUseCaseTest {

    @Mock
    private ISharingRepository sharingRepository;

    @Mock
    private ITodoListRepository todoListRepository;

    @Mock
    private IUserRepository userRepository;

    @InjectMocks
    private SharingUseCase sharingUseCase;

    private Sharing testSharing;
    private TodoList testTodoList;
    private UUID sharingId;
    private UUID listId;
    private UUID userId;
    private User testUser;

    @BeforeEach
    void setUp() {
        sharingId = UUID.randomUUID();
        listId = UUID.randomUUID();
        userId = UUID.randomUUID();

        testUser = User.builder()
                .id(userId)
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .nickname("johndoe")
                .lists(new ArrayList<>())
                .build();

        testTodoList = TodoList.builder()
                .id(listId)
                .name("Test List")
                .build();

        testSharing = Sharing.builder()
                .id(sharingId)
                .list(testTodoList)
                .users(new ArrayList<>())
                .build();
    }

    @Test
    void testCreateSharing_WithValidSharing_ShouldReturnCreatedSharing() {
        // Given
        when(todoListRepository.findById(listId)).thenReturn(Optional.of(testTodoList));
        when(sharingRepository.save(any(Sharing.class))).thenReturn(testSharing);

        // When
        Sharing result = sharingUseCase.createSharing(listId);

        // Then
        assertNotNull(result);
        assertEquals(listId, result.getList().getId());
        verify(todoListRepository).findById(listId);
        verify(sharingRepository).save(any(Sharing.class));
    }

    @Test
    void testCreateSharing_WithNonExistentList_ShouldThrowException() {
        // Given
        when(todoListRepository.findById(listId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> sharingUseCase.createSharing(listId));
        verify(todoListRepository).findById(listId);
        verify(sharingRepository, never()).save(any());
    }

    @Test
    void testGetSharingById_WithExistingSharing_ShouldReturnSharing() {
        // Given
        when(sharingRepository.findById(sharingId)).thenReturn(Optional.of(testSharing));

        // When
        Sharing result = sharingUseCase.getSharingById(sharingId);

        // Then
        assertNotNull(result);
        assertEquals(sharingId, result.getId());
        verify(sharingRepository).findById(sharingId);
    }

    @Test
    void testGetSharingById_WithNonExistentSharing_ShouldThrowException() {
        // Given
        when(sharingRepository.findById(sharingId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> sharingUseCase.getSharingById(sharingId));
        verify(sharingRepository).findById(sharingId);
    }

    @Test
    void testGetSharingByTodoListId_WithExistingSharing_ShouldReturnSharing() {
        // Given
        when(sharingRepository.findByTodoListId(listId)).thenReturn(Optional.of(testSharing));

        // When
        Sharing result = sharingUseCase.getSharingByTodoListId(listId);

        // Then
        assertNotNull(result);
        assertEquals(listId, result.getList().getId());
        verify(sharingRepository).findByTodoListId(listId);
    }

    @Test
    void testGetSharingByTodoListId_WithNonExistentSharing_ShouldThrowException() {
        // Given
        when(sharingRepository.findByTodoListId(listId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> sharingUseCase.getSharingByTodoListId(listId));
        verify(sharingRepository).findByTodoListId(listId);
    }

    @Test
    void testGetAllSharings_ShouldReturnAllSharings() {
        // Given
        TodoList todoList2 = TodoList.builder()
                .id(UUID.randomUUID())
                .name("Test List 2")
                .build();

        Sharing sharing2 = Sharing.builder()
                .id(UUID.randomUUID())
                .list(todoList2)
                .users(new ArrayList<>())
                .build();

        List<Sharing> sharings = Arrays.asList(testSharing, sharing2);
        when(sharingRepository.findAll()).thenReturn(sharings);

        // When
        List<Sharing> result = sharingUseCase.getAllSharings();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(sharingRepository).findAll();
    }

    @Test
    void testAddUserToSharing_WithValidData_ShouldAddUser() {
        // Given
        when(sharingRepository.findById(sharingId)).thenReturn(Optional.of(testSharing));
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(sharingRepository.save(any(Sharing.class))).thenReturn(testSharing);

        // When
        Sharing result = sharingUseCase.addUserToSharing(sharingId, userId);

        // Then
        assertNotNull(result);
        verify(sharingRepository).findById(sharingId);
        verify(userRepository).findById(userId);
        verify(sharingRepository).save(any(Sharing.class));
    }

    @Test
    void testAddUserToSharing_WithNonExistentSharing_ShouldThrowException() {
        // Given
        when(sharingRepository.findById(sharingId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> sharingUseCase.addUserToSharing(sharingId, userId));
        verify(sharingRepository).findById(sharingId);
        verify(userRepository, never()).findById(any());
        verify(sharingRepository, never()).save(any());
    }

    @Test
    void testAddUserToSharing_WithNonExistentUser_ShouldThrowException() {
        // Given
        when(sharingRepository.findById(sharingId)).thenReturn(Optional.of(testSharing));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> sharingUseCase.addUserToSharing(sharingId, userId));
        verify(sharingRepository).findById(sharingId);
        verify(userRepository).findById(userId);
        verify(sharingRepository, never()).save(any());
    }

    @Test
    void testRemoveUserFromSharing_WithValidData_ShouldRemoveUser() {
        // Given
        testSharing.addUser(testUser);
        when(sharingRepository.findById(sharingId)).thenReturn(Optional.of(testSharing));
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(sharingRepository.save(any(Sharing.class))).thenReturn(testSharing);

        // When
        Sharing result = sharingUseCase.removeUserFromSharing(sharingId, userId);

        // Then
        assertNotNull(result);
        verify(sharingRepository).findById(sharingId);
        verify(userRepository).findById(userId);
        verify(sharingRepository).save(any(Sharing.class));
    }

    @Test
    void testDeleteSharing_WithExistingSharing_ShouldDeleteSharing() {
        // Given
        when(sharingRepository.existsById(sharingId)).thenReturn(true);
        doNothing().when(sharingRepository).deleteById(sharingId);

        // When
        sharingUseCase.deleteSharing(sharingId);

        // Then
        verify(sharingRepository).existsById(sharingId);
        verify(sharingRepository).deleteById(sharingId);
    }

    @Test
    void testDeleteSharing_WithNonExistentSharing_ShouldThrowException() {
        // Given
        when(sharingRepository.existsById(sharingId)).thenReturn(false);

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> sharingUseCase.deleteSharing(sharingId));
        verify(sharingRepository).existsById(sharingId);
        verify(sharingRepository, never()).deleteById(any());
    }
}
