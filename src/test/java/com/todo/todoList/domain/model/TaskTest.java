package com.todo.todoList.domain.model;

import com.todo.todoList.domain.enums.Status;
import com.todo.todoList.domain.enums.TaskType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Task domain entity
 * Tests business logic without framework dependencies
 */
class TaskTest {

    private Task task;
    private TodoList todoList;
    private UUID taskId;
    private UUID listId;

    @BeforeEach
    void setUp() {
        taskId = UUID.randomUUID();
        listId = UUID.randomUUID();

        todoList = TodoList.builder()
                .id(listId)
                .name("Test List")
                .build();

        task = Task.builder()
                .id(taskId)
                .description("Test Description")
                .status(Status.PENDING)
                .type(TaskType.IMPORTANT)
                .date(LocalDate.now().plusDays(7))
                .list(todoList)
                .build();
    }

    @Test
    void testMarkAsCompleted_ShouldChangeStatusToCompleted() {
        // When
        task.markAsCompleted();

        // Then
        assertEquals(Status.COMPLETED, task.getStatus());
        assertNotNull(task.getCompleted());
    }

    @Test
    void testMarkAsPending_ShouldChangeStatusToPending() {
        // Given
        task.setStatus(Status.COMPLETED);
        task.setCompleted(LocalDate.now());

        // When
        task.markAsPending();

        // Then
        assertEquals(Status.PENDING, task.getStatus());
        assertNull(task.getCompleted());
    }

    @Test
    void testIsCompleted_WhenStatusCompleted_ShouldReturnTrue() {
        // Given
        task.setStatus(Status.COMPLETED);

        // When
        boolean result = task.isCompleted();

        // Then
        assertTrue(result);
    }

    @Test
    void testIsCompleted_WhenStatusPending_ShouldReturnFalse() {
        // When
        boolean result = task.isCompleted();

        // Then
        assertFalse(result);
    }

    @Test
    void testIsOverdue_WhenDateInPast_ShouldReturnTrue() {
        // Given
        task.setDate(LocalDate.now().minusDays(1));

        // When
        boolean result = task.isOverdue();

        // Then
        assertTrue(result);
    }

    @Test
    void testIsOverdue_WhenDateInFuture_ShouldReturnFalse() {
        // Given
        task.setDate(LocalDate.now().plusDays(1));

        // When
        boolean result = task.isOverdue();

        // Then
        assertFalse(result);
    }

    @Test
    void testIsOverdue_WhenDateIsToday_ShouldReturnFalse() {
        // Given
        task.setDate(LocalDate.now());

        // When
        boolean result = task.isOverdue();

        // Then
        assertFalse(result);
    }

    @Test
    void testIsOverdue_WhenNoDate_ShouldReturnFalse() {
        // Given
        task.setDate(null);

        // When
        boolean result = task.isOverdue();

        // Then
        assertFalse(result);
    }

    @Test
    void testIsOverdue_WhenCompletedAndOverdue_ShouldReturnFalse() {
        // Given
        task.setDate(LocalDate.now().minusDays(1));
        task.setStatus(Status.COMPLETED);

        // When
        boolean result = task.isOverdue();

        // Then
        assertFalse(result);
    }

    @Test
    void testBuilder_ShouldCreateTaskWithAllFields() {
        // When
        Task newTask = Task.builder()
                .id(taskId)
                .description("New Description")
                .status(Status.PENDING)
                .type(TaskType.FEATURED)
                .date(LocalDate.now())
                .list(todoList)
                .build();

        // Then
        assertNotNull(newTask);
        assertEquals(taskId, newTask.getId());
        assertEquals("New Description", newTask.getDescription());
        assertEquals(Status.PENDING, newTask.getStatus());
        assertEquals(TaskType.FEATURED, newTask.getType());
        assertEquals(listId, newTask.getList().getId());
    }
}
