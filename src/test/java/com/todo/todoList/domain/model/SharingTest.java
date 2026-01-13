package com.todo.todoList.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Sharing domain entity
 * Tests business logic for managing shared users
 */
class SharingTest {

    private Sharing sharing;
    private UUID sharingId;
    private UUID listId;

    @BeforeEach
    void setUp() {
        sharingId = UUID.randomUUID();
        listId = UUID.randomUUID();

        TodoList todoList = TodoList.builder()
                .id(listId)
                .name("Test List")
                .build();

        sharing = Sharing.builder()
                .id(sharingId)
                .list(todoList)
                .users(new ArrayList<>())
                .build();
    }

    @Test
    void testAddUser_ShouldAddUserToSharing() {
        // Given
        User user = createUser("john@example.com", "johndoe");

        // When
        sharing.addUser(user);

        // Then
        assertEquals(1, sharing.getUsers().size());
        assertTrue(sharing.getUsers().contains(user));
    }

    @Test
    void testAddUser_WithNullUser_ShouldNotAddUser() {
        // When
        sharing.addUser(null);

        // Then
        assertEquals(0, sharing.getUsers().size());
    }

    @Test
    void testRemoveUser_ShouldRemoveUserFromSharing() {
        // Given
        User user = createUser("john@example.com", "johndoe");
        sharing.addUser(user);

        // When
        sharing.removeUser(user);

        // Then
        assertEquals(0, sharing.getUsers().size());
        assertFalse(sharing.getUsers().contains(user));
    }

    @Test
    void testRemoveUser_WithNullUser_ShouldNotThrowException() {
        // When & Then
        assertDoesNotThrow(() -> sharing.removeUser(null));
    }

    @Test
    void testHasUser_WhenUserExists_ShouldReturnTrue() {
        // Given
        User user = createUser("john@example.com", "johndoe");
        sharing.addUser(user);

        // When
        boolean result = sharing.hasUser(user);

        // Then
        assertTrue(result);
    }

    @Test
    void testHasUser_WhenUserDoesNotExist_ShouldReturnFalse() {
        // Given
        User user = createUser("john@example.com", "johndoe");

        // When
        boolean result = sharing.hasUser(user);

        // Then
        assertFalse(result);
    }

    @Test
    void testHasUser_WithNullUser_ShouldReturnFalse() {
        // When
        boolean result = sharing.hasUser(null);

        // Then
        assertFalse(result);
    }

    @Test
    void testGetUserCount_WithMultipleUsers_ShouldReturnCorrectCount() {
        // Given
        sharing.addUser(createUser("user1@example.com", "user1"));
        sharing.addUser(createUser("user2@example.com", "user2"));
        sharing.addUser(createUser("user3@example.com", "user3"));

        // When
        int count = sharing.getUserCount();

        // Then
        assertEquals(3, count);
    }

    @Test
    void testGetUserCount_WithNoUsers_ShouldReturnZero() {
        // When
        int count = sharing.getUserCount();

        // Then
        assertEquals(0, count);
    }

    @Test
    void testAddMultipleUsers_ShouldAddAllUsers() {
        // Given
        User user1 = createUser("user1@example.com", "user1");
        User user2 = createUser("user2@example.com", "user2");
        User user3 = createUser("user3@example.com", "user3");

        // When
        sharing.addUser(user1);
        sharing.addUser(user2);
        sharing.addUser(user3);

        // Then
        assertEquals(3, sharing.getUserCount());
        assertTrue(sharing.hasUser(user1));
        assertTrue(sharing.hasUser(user2));
        assertTrue(sharing.hasUser(user3));
    }

    @Test
    void testBuilder_ShouldCreateSharingWithAllFields() {
        // Given
        TodoList todoList = TodoList.builder()
                .id(listId)
                .name("Test List")
                .build();

        // When
        Sharing newSharing = Sharing.builder()
                .id(sharingId)
                .list(todoList)
                .users(new ArrayList<>())
                .build();

        // Then
        assertNotNull(newSharing);
        assertEquals(sharingId, newSharing.getId());
        assertEquals(listId, newSharing.getList().getId());
        assertNotNull(newSharing.getUsers());
    }

    private User createUser(String email, String nickname) {
        return User.builder()
                .id(UUID.randomUUID())
                .firstName("Test")
                .lastName("User")
                .email(email)
                .nickname(nickname)
                .lists(new ArrayList<>())
                .build();
    }
}
