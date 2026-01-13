package com.todo.todoList.domain.model;

import com.todo.todoList.domain.enums.ListType;
import com.todo.todoList.domain.enums.Status;
import com.todo.todoList.domain.enums.TaskType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TodoList domain entity
 * Tests business logic for managing tasks
 */
class TodoListTest {

    private TodoList todoList;
    private UUID listId;
    private UUID userId;

    @BeforeEach
    void setUp() {
        listId = UUID.randomUUID();
        userId = UUID.randomUUID();

        User user = User.builder()
                .id(userId)
                .firstName("Test")
                .lastName("User")
                .email("test@example.com")
                .nickname("testuser")
                .build();

        todoList = TodoList.builder()
                .id(listId)
                .name("My Todo List")
                .listType(ListType.PERSONAL)
                .color("#FF5733")
                .user(user)
                .tasks(new ArrayList<>())
                .build();
    }

    @Test
    void testAddTask_ShouldAddTaskToList() {
        // Given
        Task task = createTask("Task 1", Status.PENDING);

        // When
        todoList.addTask(task);

        // Then
        assertEquals(1, todoList.getTasks().size());
        assertTrue(todoList.getTasks().contains(task));
    }

    @Test
    void testAddTask_WithNullTask_ShouldNotAddTask() {
        // When
        todoList.addTask(null);

        // Then
        assertEquals(0, todoList.getTasks().size());
    }

    @Test
    void testRemoveTask_ShouldRemoveTaskFromList() {
        // Given
        Task task = createTask("Task 1", Status.PENDING);
        todoList.addTask(task);

        // When
        todoList.removeTask(task);

        // Then
        assertEquals(0, todoList.getTasks().size());
        assertFalse(todoList.getTasks().contains(task));
    }

    @Test
    void testRemoveTask_WithNullTask_ShouldNotThrowException() {
        // When & Then
        assertDoesNotThrow(() -> todoList.removeTask(null));
    }

    @Test
    void testGetTotalTasks_WithMultipleTasks_ShouldReturnCorrectCount() {
        // Given
        todoList.addTask(createTask("Task 1", Status.PENDING));
        todoList.addTask(createTask("Task 2", Status.COMPLETED));
        todoList.addTask(createTask("Task 3", Status.PENDING));

        // When
        int total = todoList.getTotalTasks();

        // Then
        assertEquals(3, total);
    }

    @Test
    void testGetTotalTasks_WithNoTasks_ShouldReturnZero() {
        // When
        int total = todoList.getTotalTasks();

        // Then
        assertEquals(0, total);
    }

    @Test
    void testGetCompletedTasksCount_WithMixedStatuses_ShouldReturnCorrectCount() {
        // Given
        todoList.addTask(createTask("Task 1", Status.PENDING));
        todoList.addTask(createTask("Task 2", Status.COMPLETED));
        todoList.addTask(createTask("Task 3", Status.COMPLETED));
        todoList.addTask(createTask("Task 4", Status.PENDING));

        // When
        long completedCount = todoList.getCompletedTasksCount();

        // Then
        assertEquals(2, completedCount);
    }

    @Test
    void testGetCompletedTasksCount_WithAllPending_ShouldReturnZero() {
        // Given
        todoList.addTask(createTask("Task 1", Status.PENDING));
        todoList.addTask(createTask("Task 2", Status.PENDING));

        // When
        long completedCount = todoList.getCompletedTasksCount();

        // Then
        assertEquals(0, completedCount);
    }

    @Test
    void testGetCompletedTasksCount_WithAllCompleted_ShouldReturnAllCount() {
        // Given
        todoList.addTask(createTask("Task 1", Status.COMPLETED));
        todoList.addTask(createTask("Task 2", Status.COMPLETED));

        // When
        long completedCount = todoList.getCompletedTasksCount();

        // Then
        assertEquals(2, completedCount);
    }

    @Test
    void testGetCompletedTasksCount_WithNoTasks_ShouldReturnZero() {
        // When
        long completedCount = todoList.getCompletedTasksCount();

        // Then
        assertEquals(0, completedCount);
    }

    @Test
    void testBuilder_ShouldCreateTodoListWithAllFields() {
        // Given
        User user = User.builder()
                .id(userId)
                .firstName("Test")
                .lastName("User")
                .email("test@example.com")
                .nickname("testuser")
                .build();

        // When
        TodoList newList = TodoList.builder()
                .id(listId)
                .name("Test List")
                .listType(ListType.SHARED)
                .color("#0000FF")
                .user(user)
                .tasks(new ArrayList<>())
                .build();

        // Then
        assertNotNull(newList);
        assertEquals(listId, newList.getId());
        assertEquals("Test List", newList.getName());
        assertEquals(ListType.SHARED, newList.getListType());
        assertEquals("#0000FF", newList.getColor());
        assertEquals(userId, newList.getUser().getId());
        assertNotNull(newList.getTasks());
    }

    private Task createTask(String title, Status status) {
        return Task.builder()
                .id(UUID.randomUUID())
                .description("Description for " + title)
                .status(status)
                .type(TaskType.IMPORTANT)
                .date(LocalDate.now().plusDays(7))
                .list(todoList)
                .build();
    }
}
