package com.todo.todoList.application.usecase;

import com.todo.todoList.domain.enums.Status;
import com.todo.todoList.domain.enums.TaskType;
import com.todo.todoList.domain.exception.EntityNotFoundException;
import com.todo.todoList.domain.model.Task;
import com.todo.todoList.domain.model.TodoList;
import com.todo.todoList.domain.port.out.ITaskRepository;
import com.todo.todoList.domain.port.out.ITodoListRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for TaskUseCase
 * Uses mocks to test use case logic in isolation
 */
@ExtendWith(MockitoExtension.class)
class TaskUseCaseTest {

    @Mock
    private ITaskRepository taskRepository;

    @Mock
    private ITodoListRepository todoListRepository;

    @InjectMocks
    private TaskUseCase taskUseCase;

    private Task testTask;
    private TodoList testTodoList;
    private UUID taskId;
    private UUID listId;

    @BeforeEach
    void setUp() {
        taskId = UUID.randomUUID();
        listId = UUID.randomUUID();

        testTodoList = TodoList.builder()
                .id(listId)
                .name("Test List")
                .build();

        testTask = Task.builder()
                .id(taskId)
                .description("Test Description")
                .status(Status.PENDING)
                .type(TaskType.IMPORTANT)
                .date(LocalDate.now().plusDays(7))
                .list(testTodoList)
                .build();
    }

    @Test
    void testCreateTask_WithValidTask_ShouldReturnCreatedTask() {
        // Given
        when(todoListRepository.findById(listId)).thenReturn(Optional.of(testTodoList));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        // When
        Task result = taskUseCase.createTask(testTask, listId);

        // Then
        assertNotNull(result);
        assertEquals(testTask.getDescription(), result.getDescription());
        assertEquals(listId, result.getList().getId());
        verify(todoListRepository).findById(listId);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void testCreateTask_WithNonExistentList_ShouldThrowException() {
        // Given
        when(todoListRepository.findById(listId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> taskUseCase.createTask(testTask, listId));
        verify(todoListRepository).findById(listId);
        verify(taskRepository, never()).save(any());
    }

    @Test
    void testUpdateTask_WithValidTask_ShouldReturnUpdatedTask() {
        // Given
        Task updatedTask = Task.builder()
                .id(taskId)
                .description("Updated Description")
                .status(Status.COMPLETED)
                .type(TaskType.FEATURED)
                .date(LocalDate.now().plusDays(14))
                .list(testTodoList)
                .build();

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);

        // When
        Task result = taskUseCase.updateTask(taskId, updatedTask);

        // Then
        assertNotNull(result);
        assertEquals("Updated Description", result.getDescription());
        assertEquals(Status.COMPLETED, result.getStatus());
        verify(taskRepository).findById(taskId);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void testUpdateTask_WithNonExistentTask_ShouldThrowException() {
        // Given
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> taskUseCase.updateTask(taskId, testTask));
        verify(taskRepository).findById(taskId);
        verify(taskRepository, never()).save(any());
    }

    @Test
    void testGetTaskById_WithExistingTask_ShouldReturnTask() {
        // Given
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));

        // When
        Optional<Task> result = taskUseCase.getTaskById(taskId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(taskId, result.get().getId());
        assertEquals(testTask.getDescription(), result.get().getDescription());
        verify(taskRepository).findById(taskId);
    }

    @Test
    void testGetTaskById_WithNonExistentTask_ShouldReturnEmpty() {
        // Given
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        // When
        Optional<Task> result = taskUseCase.getTaskById(taskId);

        // Then
        assertFalse(result.isPresent());
        verify(taskRepository).findById(taskId);
    }

    @Test
    void testGetTasksByTodoListId_ShouldReturnListTasks() {
        // Given
        Task task2 = Task.builder()
                .id(UUID.randomUUID())
                .description("Second Task")
                .status(Status.PENDING)
                .type(TaskType.IMPORTANT)
                .date(LocalDate.now().plusDays(3))
                .list(testTodoList)
                .build();

        List<Task> tasks = Arrays.asList(testTask, task2);
        when(todoListRepository.existsById(listId)).thenReturn(true);
        when(taskRepository.findByTodoListId(listId)).thenReturn(tasks);

        // When
        List<Task> result = taskUseCase.getTasksByTodoListId(listId);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(todoListRepository).existsById(listId);
        verify(taskRepository).findByTodoListId(listId);
    }

    @Test
    void testGetTasksByTodoListId_WithNonExistentList_ShouldThrowException() {
        // Given
        when(todoListRepository.existsById(listId)).thenReturn(false);

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> taskUseCase.getTasksByTodoListId(listId));
        verify(todoListRepository).existsById(listId);
        verify(taskRepository, never()).findByTodoListId(any());
    }

    @Test
    void testGetTasksByType_ShouldReturnFilteredTasks() {
        // Given
        Task task2 = Task.builder()
                .id(UUID.randomUUID())
                .description("Event Task")
                .status(Status.PENDING)
                .type(TaskType.IMPORTANT)
                .date(LocalDate.now().plusDays(5))
                .list(testTodoList)
                .build();

        List<Task> tasks = Arrays.asList(testTask, task2);
        when(taskRepository.findByType(TaskType.IMPORTANT)).thenReturn(tasks);

        // When
        List<Task> result = taskUseCase.getTasksByType(TaskType.IMPORTANT);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(taskRepository).findByType(TaskType.IMPORTANT);
    }

    @Test
    void testMarkAsCompleted_WithExistingTask_ShouldMarkCompleted() {
        // Given
        Task completedTask = Task.builder()
                .id(taskId)
                .description(testTask.getDescription())
                .status(Status.COMPLETED)
                .type(testTask.getType())
                .date(testTask.getDate())
                .list(testTask.getList())
                .completed(LocalDate.now())
                .build();

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(Task.class))).thenReturn(completedTask);

        // When
        Task result = taskUseCase.markAsCompleted(taskId);

        // Then
        assertNotNull(result);
        assertEquals(Status.COMPLETED, result.getStatus());
        verify(taskRepository).findById(taskId);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void testMarkAsCompleted_WithNonExistentTask_ShouldThrowException() {
        // Given
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> taskUseCase.markAsCompleted(taskId));
        verify(taskRepository).findById(taskId);
        verify(taskRepository, never()).save(any());
    }

    @Test
    void testMarkAsPending_WithExistingTask_ShouldMarkPending() {
        // Given
        testTask.setStatus(Status.COMPLETED);
        Task pendingTask = Task.builder()
                .id(taskId)
                .description(testTask.getDescription())
                .status(Status.PENDING)
                .type(testTask.getType())
                .date(testTask.getDate())
                .list(testTask.getList())
                .build();

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(Task.class))).thenReturn(pendingTask);

        // When
        Task result = taskUseCase.markAsPending(taskId);

        // Then
        assertNotNull(result);
        assertEquals(Status.PENDING, result.getStatus());
        verify(taskRepository).findById(taskId);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void testDeleteTask_WithExistingTask_ShouldDeleteTask() {
        // Given
        when(taskRepository.existsById(taskId)).thenReturn(true);
        doNothing().when(taskRepository).deleteById(taskId);

        // When
        taskUseCase.deleteTask(taskId);

        // Then
        verify(taskRepository).existsById(taskId);
        verify(taskRepository).deleteById(taskId);
    }

    @Test
    void testDeleteTask_WithNonExistentTask_ShouldThrowException() {
        // Given
        when(taskRepository.existsById(taskId)).thenReturn(false);

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> taskUseCase.deleteTask(taskId));
        verify(taskRepository).existsById(taskId);
        verify(taskRepository, never()).deleteById(any());
    }
}
