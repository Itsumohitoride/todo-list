package com.todo.todoList.application.usecase;

import com.todo.todoList.application.dto.SharingDTO;
import com.todo.todoList.domain.exception.DuplicateEntityException;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private String shareToken;

    @BeforeEach
    void setUp() {
        sharingId = UUID.randomUUID();
        listId = UUID.randomUUID();
        userId = UUID.randomUUID();
        shareToken = UUID.randomUUID().toString();

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
                .shareToken(shareToken)
                .list(testTodoList)
                .createdAt(LocalDateTime.now())
                .users(new ArrayList<>())
                .build();
    }

    @Test
    void testCreateSharing_WithValidList_ShouldReturnCreatedSharing() {
        // Given
        when(todoListRepository.findById(listId)).thenReturn(Optional.of(testTodoList));
        when(sharingRepository.findByTodoListId(listId)).thenReturn(Optional.empty());
        when(sharingRepository.save(any(Sharing.class))).thenReturn(testSharing);

        // When
        SharingDTO result = sharingUseCase.createSharing(listId);

        // Then
        assertNotNull(result);
        assertNotNull(result.getShareToken());
        assertEquals(listId, result.getTodoListId());
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
    void testCreateSharing_WithExistingSharing_ShouldThrowException() {
        // Given
        when(todoListRepository.findById(listId)).thenReturn(Optional.of(testTodoList));
        when(sharingRepository.findByTodoListId(listId)).thenReturn(Optional.of(testSharing));

        // When & Then
        assertThrows(DuplicateEntityException.class, () -> sharingUseCase.createSharing(listId));
        verify(sharingRepository, never()).save(any());
    }

    @Test
    void testGetSharingById_WithExistingSharing_ShouldReturnSharing() {
        // Given
        when(sharingRepository.findById(sharingId)).thenReturn(Optional.of(testSharing));

        // When
        SharingDTO result = sharingUseCase.getSharingById(sharingId);

        // Then
        assertNotNull(result);
        assertEquals(sharingId, result.getId());
        assertEquals(shareToken, result.getShareToken());
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
    void testGetSharingByToken_WithExistingToken_ShouldReturnSharing() {
        // Given
        when(sharingRepository.findByShareToken(shareToken)).thenReturn(Optional.of(testSharing));

        // When
        SharingDTO result = sharingUseCase.getSharingByToken(shareToken);

        // Then
        assertNotNull(result);
        assertEquals(shareToken, result.getShareToken());
        verify(sharingRepository).findByShareToken(shareToken);
    }

    @Test
    void testGetSharingByToken_WithNonExistentToken_ShouldThrowException() {
        // Given
        when(sharingRepository.findByShareToken(shareToken)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> sharingUseCase.getSharingByToken(shareToken));
        verify(sharingRepository).findByShareToken(shareToken);
    }

    @Test
    void testGetSharingByTodoListId_WithExistingSharing_ShouldReturnSharing() {
        // Given
        when(sharingRepository.findByTodoListId(listId)).thenReturn(Optional.of(testSharing));

        // When
        SharingDTO result = sharingUseCase.getSharingByTodoListId(listId);

        // Then
        assertNotNull(result);
        assertEquals(listId, result.getTodoListId());
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
    void testJoinSharedList_WithValidData_ShouldAddUser() {
        // Given
        when(sharingRepository.findByShareToken(shareToken)).thenReturn(Optional.of(testSharing));
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(sharingRepository.save(any(Sharing.class))).thenReturn(testSharing);

        // When
        SharingDTO result = sharingUseCase.joinSharedList(shareToken, userId);

        // Then
        assertNotNull(result);
        verify(sharingRepository).findByShareToken(shareToken);
        verify(userRepository).findById(userId);
        verify(sharingRepository).save(any(Sharing.class));
    }

    @Test
    void testJoinSharedList_WithNonExistentToken_ShouldThrowException() {
        // Given
        when(sharingRepository.findByShareToken(shareToken)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> sharingUseCase.joinSharedList(shareToken, userId));
        verify(sharingRepository).findByShareToken(shareToken);
        verify(userRepository, never()).findById(any());
    }

    @Test
    void testJoinSharedList_WithNonExistentUser_ShouldThrowException() {
        // Given
        when(sharingRepository.findByShareToken(shareToken)).thenReturn(Optional.of(testSharing));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> sharingUseCase.joinSharedList(shareToken, userId));
        verify(userRepository).findById(userId);
        verify(sharingRepository, never()).save(any());
    }

    @Test
    void testJoinSharedList_WithDuplicateUser_ShouldThrowException() {
        // Given
        testSharing.addUser(testUser); // User already in sharing
        when(sharingRepository.findByShareToken(shareToken)).thenReturn(Optional.of(testSharing));
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        // When & Then
        assertThrows(DuplicateEntityException.class, () -> sharingUseCase.joinSharedList(shareToken, userId));
        verify(sharingRepository, never()).save(any());
    }

    @Test
    void testGetSharedUsers_WithExistingSharing_ShouldReturnUserIds() {
        // Given
        testSharing.addUser(testUser);
        when(sharingRepository.findByTodoListId(listId)).thenReturn(Optional.of(testSharing));

        // When
        List<UUID> result = sharingUseCase.getSharedUsers(listId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(userId));
        verify(sharingRepository).findByTodoListId(listId);
    }

    @Test
    void testGenerateQRCode_WithValidToken_ShouldReturnByteArray() {
        // Given
        when(sharingRepository.existsByShareToken(shareToken)).thenReturn(true);

        // When
        byte[] result = sharingUseCase.generateQRCode(shareToken, 300, 300);

        // Then
        assertNotNull(result);
        assertTrue(result.length > 0);
        verify(sharingRepository).existsByShareToken(shareToken);
    }

    @Test
    void testGenerateQRCode_WithNonExistentToken_ShouldThrowException() {
        // Given
        when(sharingRepository.existsByShareToken(shareToken)).thenReturn(false);

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> sharingUseCase.generateQRCode(shareToken, 300, 300));
        verify(sharingRepository).existsByShareToken(shareToken);
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
