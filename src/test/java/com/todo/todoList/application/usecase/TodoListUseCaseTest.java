package com.todo.todoList.application.usecase;

import com.todo.todoList.domain.enums.ListType;
import com.todo.todoList.domain.exception.EntityNotFoundException;
import com.todo.todoList.domain.model.TodoList;
import com.todo.todoList.domain.model.User;
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
 * Unit tests for TodoListUseCase
 * Uses mocks to test use case logic in isolation
 */
@ExtendWith(MockitoExtension.class)
class TodoListUseCaseTest {

    @Mock
    private ITodoListRepository todoListRepository;

    @Mock
    private IUserRepository userRepository;

    @InjectMocks
    private TodoListUseCase todoListUseCase;

    private TodoList testTodoList;
    private User testUser;
    private UUID listId;
    private UUID userId;

    @BeforeEach
    void setUp() {
        listId = UUID.randomUUID();
        userId = UUID.randomUUID();

        testUser = User.builder()
                .id(userId)
                .firstName("Test")
                .lastName("User")
                .email("test@example.com")
                .nickname("testuser")
                .lists(new ArrayList<>())
                .build();

        testTodoList = TodoList.builder()
                .id(listId)
                .name("My Todo List")
                .listType(ListType.PERSONAL)
                .color("#FF5733")
                .user(testUser)
                .tasks(new ArrayList<>())
                .build();
    }

    @Test
    void testCreateTodoList_WithValidList_ShouldReturnCreatedList() {
        // Given
        when(userRepository.existsById(userId)).thenReturn(true);
        when(todoListRepository.save(any(TodoList.class))).thenReturn(testTodoList);

        // When
        TodoList result = todoListUseCase.createTodoList(testTodoList);

        // Then
        assertNotNull(result);
        assertEquals(testTodoList.getName(), result.getName());
        assertEquals(userId, result.getUser().getId());
        verify(userRepository).existsById(userId);
        verify(todoListRepository).save(testTodoList);
    }

    @Test
    void testCreateTodoList_WithNonExistentUser_ShouldThrowException() {
        // Given
        when(userRepository.existsById(userId)).thenReturn(false);

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> todoListUseCase.createTodoList(testTodoList));
        verify(userRepository).existsById(userId);
        verify(todoListRepository, never()).save(any());
    }

    @Test
    void testUpdateTodoList_WithValidList_ShouldReturnUpdatedList() {
        // Given
        TodoList updatedList = TodoList.builder()
                .id(listId)
                .name("Updated List")
                .listType(ListType.SHARED)
                .color("#0000FF")
                .user(testUser)
                .tasks(new ArrayList<>())
                .build();

        when(todoListRepository.findById(listId)).thenReturn(Optional.of(testTodoList));
        when(todoListRepository.save(any(TodoList.class))).thenReturn(updatedList);

        // When
        TodoList result = todoListUseCase.updateTodoList(listId, updatedList);

        // Then
        assertNotNull(result);
        assertEquals("Updated List", result.getName());
        assertEquals(ListType.SHARED, result.getListType());
        verify(todoListRepository).findById(listId);
        verify(todoListRepository).save(any(TodoList.class));
    }

    @Test
    void testUpdateTodoList_WithNonExistentList_ShouldThrowException() {
        // Given
        when(todoListRepository.findById(listId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> todoListUseCase.updateTodoList(listId, testTodoList));
        verify(todoListRepository).findById(listId);
        verify(todoListRepository, never()).save(any());
    }

    @Test
    void testGetTodoListById_WithExistingList_ShouldReturnList() {
        // Given
        when(todoListRepository.findById(listId)).thenReturn(Optional.of(testTodoList));

        // When
        TodoList result = todoListUseCase.getTodoListById(listId);

        // Then
        assertNotNull(result);
        assertEquals(listId, result.getId());
        assertEquals(testTodoList.getName(), result.getName());
        verify(todoListRepository).findById(listId);
    }

    @Test
    void testGetTodoListById_WithNonExistentList_ShouldThrowException() {
        // Given
        when(todoListRepository.findById(listId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> todoListUseCase.getTodoListById(listId));
        verify(todoListRepository).findById(listId);
    }

    @Test
    void testGetTodoListsByUserId_ShouldReturnUserLists() {
        // Given
        TodoList list2 = TodoList.builder()
                .id(UUID.randomUUID())
                .name("Second List")
                .listType(ListType.PERSONAL)
                .color("#00FF00")
                .user(testUser)
                .tasks(new ArrayList<>())
                .build();

        List<TodoList> lists = Arrays.asList(testTodoList, list2);
        when(todoListRepository.findByUserId(userId)).thenReturn(lists);

        // When
        List<TodoList> result = todoListUseCase.getTodoListsByUserId(userId);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(todoListRepository).findByUserId(userId);
    }

    @Test
    void testGetAllTodoLists_ShouldReturnAllLists() {
        // Given
        User user2 = User.builder()
                .id(UUID.randomUUID())
                .firstName("Another")
                .lastName("User")
                .email("another@example.com")
                .nickname("another")
                .build();

        TodoList list2 = TodoList.builder()
                .id(UUID.randomUUID())
                .name("Another List")
                .listType(ListType.SHARED)
                .color("#FFFF00")
                .user(user2)
                .tasks(new ArrayList<>())
                .build();

        List<TodoList> lists = Arrays.asList(testTodoList, list2);
        when(todoListRepository.findAll()).thenReturn(lists);

        // When
        List<TodoList> result = todoListUseCase.getAllTodoLists();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(todoListRepository).findAll();
    }

    @Test
    void testDeleteTodoList_WithExistingList_ShouldDeleteList() {
        // Given
        when(todoListRepository.existsById(listId)).thenReturn(true);
        doNothing().when(todoListRepository).deleteById(listId);

        // When
        todoListUseCase.deleteTodoList(listId);

        // Then
        verify(todoListRepository).existsById(listId);
        verify(todoListRepository).deleteById(listId);
    }

    @Test
    void testDeleteTodoList_WithNonExistentList_ShouldThrowException() {
        // Given
        when(todoListRepository.existsById(listId)).thenReturn(false);

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> todoListUseCase.deleteTodoList(listId));
        verify(todoListRepository).existsById(listId);
        verify(todoListRepository, never()).deleteById(any());
    }
}
