package com.todo.todoList.infrastructure.adapter.out.persistence;

import com.todo.todoList.domain.model.User;
import com.todo.todoList.infrastructure.adapter.out.persistence.adapter.UserRepositoryAdapter;
import com.todo.todoList.infrastructure.adapter.out.persistence.entity.UserJpaEntity;
import com.todo.todoList.infrastructure.adapter.out.persistence.repository.UserJpaRepository;
import com.todo.todoList.infrastructure.mapper.UserMapper;
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
 * Unit tests for UserRepositoryAdapter
 * Tests the adapter's integration with JPA repository and mapper
 */
@ExtendWith(MockitoExtension.class)
class UserRepositoryAdapterTest {

    @Mock
    private UserJpaRepository jpaRepository;

    @Mock
    private UserMapper mapper;

    @InjectMocks
    private UserRepositoryAdapter repositoryAdapter;

    private User domainUser;
    private UserJpaEntity jpaEntity;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        domainUser = User.builder()
                .id(userId)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .nickname("johndoe")
                .lists(new ArrayList<>())
                .build();

        jpaEntity = UserJpaEntity.builder()
                .id(userId)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .nickname("johndoe")
                .lists(new ArrayList<>())
                .build();
    }

    @Test
    void testSave_NewUser_ShouldCreateNewEntity() {
        // Given
        when(jpaRepository.existsById(userId)).thenReturn(false);
        when(mapper.toJpaEntity(domainUser)).thenReturn(jpaEntity);
        when(jpaRepository.save(jpaEntity)).thenReturn(jpaEntity);
        when(mapper.toDomain(jpaEntity)).thenReturn(domainUser);

        // When
        User result = repositoryAdapter.save(domainUser);

        // Then
        assertNotNull(result);
        assertEquals(domainUser.getId(), result.getId());
        verify(jpaRepository).existsById(userId);
        verify(mapper).toJpaEntity(domainUser);
        verify(jpaRepository).save(jpaEntity);
        verify(mapper).toDomain(jpaEntity);
    }

    @Test
    void testSave_ExistingUser_ShouldUpdateEntity() {
        // Given
        when(jpaRepository.existsById(userId)).thenReturn(true);
        when(jpaRepository.findById(userId)).thenReturn(Optional.of(jpaEntity));
        doNothing().when(mapper).updateJpaEntityFromDomain(domainUser, jpaEntity);
        when(jpaRepository.save(jpaEntity)).thenReturn(jpaEntity);
        when(mapper.toDomain(jpaEntity)).thenReturn(domainUser);

        // When
        User result = repositoryAdapter.save(domainUser);

        // Then
        assertNotNull(result);
        verify(jpaRepository).existsById(userId);
        verify(jpaRepository).findById(userId);
        verify(mapper).updateJpaEntityFromDomain(domainUser, jpaEntity);
        verify(jpaRepository).save(jpaEntity);
        verify(mapper).toDomain(jpaEntity);
    }

    @Test
    void testFindById_WithExistingUser_ShouldReturnUser() {
        // Given
        when(jpaRepository.findById(userId)).thenReturn(Optional.of(jpaEntity));
        when(mapper.toDomain(jpaEntity)).thenReturn(domainUser);

        // When
        Optional<User> result = repositoryAdapter.findById(userId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(userId, result.get().getId());
        verify(jpaRepository).findById(userId);
        verify(mapper).toDomain(jpaEntity);
    }

    @Test
    void testFindById_WithNonExistentUser_ShouldReturnEmpty() {
        // Given
        when(jpaRepository.findById(userId)).thenReturn(Optional.empty());

        // When
        Optional<User> result = repositoryAdapter.findById(userId);

        // Then
        assertFalse(result.isPresent());
        verify(jpaRepository).findById(userId);
        verify(mapper, never()).toDomain(any());
    }

    @Test
    void testFindByEmail_WithExistingEmail_ShouldReturnUser() {
        // Given
        String email = "john.doe@example.com";
        when(jpaRepository.findByEmail(email)).thenReturn(Optional.of(jpaEntity));
        when(mapper.toDomain(jpaEntity)).thenReturn(domainUser);

        // When
        Optional<User> result = repositoryAdapter.findByEmail(email);

        // Then
        assertTrue(result.isPresent());
        assertEquals(email, result.get().getEmail());
        verify(jpaRepository).findByEmail(email);
        verify(mapper).toDomain(jpaEntity);
    }

    @Test
    void testFindByNickname_WithExistingNickname_ShouldReturnUser() {
        // Given
        String nickname = "johndoe";
        when(jpaRepository.findByNickname(nickname)).thenReturn(Optional.of(jpaEntity));
        when(mapper.toDomain(jpaEntity)).thenReturn(domainUser);

        // When
        Optional<User> result = repositoryAdapter.findByNickname(nickname);

        // Then
        assertTrue(result.isPresent());
        assertEquals(nickname, result.get().getNickname());
        verify(jpaRepository).findByNickname(nickname);
        verify(mapper).toDomain(jpaEntity);
    }

    @Test
    void testFindAll_ShouldReturnAllUsers() {
        // Given
        UserJpaEntity entity2 = UserJpaEntity.builder()
                .id(UUID.randomUUID())
                .firstName("Jane")
                .lastName("Smith")
                .email("jane@example.com")
                .nickname("janesmith")
                .lists(new ArrayList<>())
                .build();

        User domainUser2 = User.builder()
                .id(entity2.getId())
                .firstName("Jane")
                .lastName("Smith")
                .email("jane@example.com")
                .nickname("janesmith")
                .lists(new ArrayList<>())
                .build();

        List<UserJpaEntity> entities = Arrays.asList(jpaEntity, entity2);
        when(jpaRepository.findAll()).thenReturn(entities);
        when(mapper.toDomain(jpaEntity)).thenReturn(domainUser);
        when(mapper.toDomain(entity2)).thenReturn(domainUser2);

        // When
        List<User> result = repositoryAdapter.findAll();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(jpaRepository).findAll();
        verify(mapper, times(2)).toDomain(any(UserJpaEntity.class));
    }

    @Test
    void testDeleteById_ShouldCallJpaRepositoryDelete() {
        // Given
        doNothing().when(jpaRepository).deleteById(userId);

        // When
        repositoryAdapter.deleteById(userId);

        // Then
        verify(jpaRepository).deleteById(userId);
    }

    @Test
    void testExistsById_WithExistingUser_ShouldReturnTrue() {
        // Given
        when(jpaRepository.existsById(userId)).thenReturn(true);

        // When
        boolean result = repositoryAdapter.existsById(userId);

        // Then
        assertTrue(result);
        verify(jpaRepository).existsById(userId);
    }

    @Test
    void testExistsById_WithNonExistentUser_ShouldReturnFalse() {
        // Given
        when(jpaRepository.existsById(userId)).thenReturn(false);

        // When
        boolean result = repositoryAdapter.existsById(userId);

        // Then
        assertFalse(result);
        verify(jpaRepository).existsById(userId);
    }
}
