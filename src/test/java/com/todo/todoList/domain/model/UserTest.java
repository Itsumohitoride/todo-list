package com.todo.todoList.domain.model;

import com.todo.todoList.domain.enums.ListType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for User domain entity
 * Tests business logic for managing todo lists
 */
class UserTest {

    private User user;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        user = User.builder()
                .id(userId)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .nickname("johndoe")
                .lists(new ArrayList<>())
                .build();
    }

    @Test
    void testAddList_ShouldAddListToUser() {
        // Given
        TodoList todoList = createTodoList("My List");

        // When
        user.addList(todoList);

        // Then
        assertEquals(1, user.getLists().size());
        assertTrue(user.getLists().contains(todoList));
    }

    @Test
    void testAddList_WithNullList_ShouldNotAddList() {
        // When
        user.addList(null);

        // Then
        assertEquals(0, user.getLists().size());
    }

    @Test
    void testRemoveList_ShouldRemoveListFromUser() {
        // Given
        TodoList todoList = createTodoList("My List");
        user.addList(todoList);

        // When
        user.removeList(todoList);

        // Then
        assertEquals(0, user.getLists().size());
        assertFalse(user.getLists().contains(todoList));
    }

    @Test
    void testRemoveList_WithNullList_ShouldNotThrowException() {
        // When & Then
        assertDoesNotThrow(() -> user.removeList(null));
    }

    @Test
    void testAddMultipleLists_ShouldAddAllLists() {
        // Given
        TodoList list1 = createTodoList("List 1");
        TodoList list2 = createTodoList("List 2");
        TodoList list3 = createTodoList("List 3");

        // When
        user.addList(list1);
        user.addList(list2);
        user.addList(list3);

        // Then
        assertEquals(3, user.getLists().size());
        assertTrue(user.getLists().contains(list1));
        assertTrue(user.getLists().contains(list2));
        assertTrue(user.getLists().contains(list3));
    }

    @Test
    void testBuilder_ShouldCreateUserWithAllFields() {
        // When
        User newUser = User.builder()
                .id(userId)
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@example.com")
                .nickname("janesmith")
                .lists(new ArrayList<>())
                .build();

        // Then
        assertNotNull(newUser);
        assertEquals(userId, newUser.getId());
        assertEquals("Jane", newUser.getFirstName());
        assertEquals("Smith", newUser.getLastName());
        assertEquals("jane.smith@example.com", newUser.getEmail());
        assertEquals("janesmith", newUser.getNickname());
        assertNotNull(newUser.getLists());
    }

    @Test
    void testBuilderWithDefaultLists_ShouldCreateEmptyList() {
        // When
        User newUser = User.builder()
                .id(userId)
                .firstName("Test")
                .lastName("User")
                .email("test@example.com")
                .nickname("testuser")
                .build();

        // Then
        assertNotNull(newUser.getLists());
        assertEquals(0, newUser.getLists().size());
    }

    private TodoList createTodoList(String name) {
        User user = User.builder()
                .id(userId)
                .firstName("Test")
                .lastName("User")
                .email("test@example.com")
                .nickname("testuser")
                .build();

        return TodoList.builder()
                .id(UUID.randomUUID())
                .name(name)
                .listType(ListType.PERSONAL)
                .color("#FF5733")
                .user(user)
                .tasks(new ArrayList<>())
                .build();
    }
}
