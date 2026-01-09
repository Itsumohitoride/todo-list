package com.todo.todoList.infrastructure.adapter.in.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todo.todoList.application.dto.UserDTO;
import com.todo.todoList.domain.exception.DuplicateEntityException;
import com.todo.todoList.domain.exception.EntityNotFoundException;
import com.todo.todoList.domain.model.User;
import com.todo.todoList.domain.port.in.IManageUserUseCase;
import com.todo.todoList.domain.port.out.IUserRepository;
import com.todo.todoList.infrastructure.exception.GlobalExceptionHandler;
import com.todo.todoList.infrastructure.validation.UniqueFieldValidator;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for UserController
 */
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private IManageUserUseCase userUseCase;

    @Mock
    private IUserRepository userRepository;

    @InjectMocks
    private UserController userController;

    private UUID userId;
    private User testUser;
    private UserDTO testUserDTO;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        // Configure custom validator factory to inject mocks into UniqueFieldValidator
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.setConstraintValidatorFactory(new ConstraintValidatorFactory() {
            @Override
            public <T extends ConstraintValidator<?, ?>> T getInstance(Class<T> key) {
                if (key == UniqueFieldValidator.class) {
                    return (T) new UniqueFieldValidator(userRepository);
                }
                try {
                    return key.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    throw new RuntimeException("Failed to instantiate validator: " + key, e);
                }
            }

            @Override
            public void releaseInstance(ConstraintValidator<?, ?> instance) {
                // No cleanup needed
            }
        });
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();

        // Mock userRepository for UniqueFieldValidator (lenient to avoid UnnecessaryStubbingException)
        lenient().when(userRepository.findByEmail(anyString())).thenReturn(java.util.Optional.empty());
        lenient().when(userRepository.findByNickname(anyString())).thenReturn(java.util.Optional.empty());

        userId = UUID.randomUUID();

        testUser = User.builder()
                .id(userId)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .nickname("johndoe")
                .build();

        testUserDTO = UserDTO.builder()
                .id(userId)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .nickname("johndoe")
                .build();
    }

    @Test
    void testCreateUser_WithValidData_ShouldReturn201() throws Exception {
        // Given
        UserDTO requestDTO = UserDTO.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .nickname("johndoe")
                .build();

        when(userUseCase.createUser(any(User.class))).thenReturn(testUser);

        // When & Then
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.nickname").value("johndoe"));

        verify(userUseCase).createUser(any(User.class));
    }

    @Test
    void testCreateUser_WithDuplicateEmail_ShouldReturn409() throws Exception {
        // Given
        UserDTO requestDTO = UserDTO.builder()
                .firstName("John")
                .lastName("Doe")
                .email("duplicate@example.com")
                .nickname("johndoe")
                .build();

        when(userUseCase.createUser(any(User.class)))
                .thenThrow(new DuplicateEntityException("Email", "duplicate@example.com"));

        // When & Then
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"));
    }

    @Test
    void testGetUserById_WithExistingUser_ShouldReturn200() throws Exception {
        // Given
        when(userUseCase.getUserById(userId)).thenReturn(testUser);

        // When & Then
        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.nickname").value("johndoe"));

        verify(userUseCase).getUserById(userId);
    }

    @Test
    void testGetUserById_WithNonExistentUser_ShouldReturn404() throws Exception {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        when(userUseCase.getUserById(nonExistentId))
                .thenThrow(new EntityNotFoundException("User", nonExistentId));

        // When & Then
        mockMvc.perform(get("/api/users/{id}", nonExistentId))
                .andDo(result -> {
                    System.out.println("Status: " + result.getResponse().getStatus());
                    System.out.println("Body: " + result.getResponse().getContentAsString());
                    System.out.println("Error: " + result.getResponse().getErrorMessage());
                })
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"));
    }

    @Test
    void testGetAllUsers_ShouldReturn200WithList() throws Exception {
        // Given
        User user2 = User.builder()
                .id(UUID.randomUUID())
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@example.com")
                .nickname("janesmith")
                .build();

        List<User> users = Arrays.asList(testUser, user2);
        when(userUseCase.getAllUsers()).thenReturn(users);

        // When & Then
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[1].firstName").value("Jane"));

        verify(userUseCase).getAllUsers();
    }

    @Test
    void testGetUserByEmail_WithExistingEmail_ShouldReturn200() throws Exception {
        // Given
        when(userUseCase.getUserByEmail("john.doe@example.com")).thenReturn(testUser);

        // When & Then
        mockMvc.perform(get("/api/users/email/{email}", "john.doe@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.firstName").value("John"));

        verify(userUseCase).getUserByEmail("john.doe@example.com");
    }

    @Test
    void testGetUserByNickname_WithExistingNickname_ShouldReturn200() throws Exception {
        // Given
        when(userUseCase.getUserByNickname("johndoe")).thenReturn(testUser);

        // When & Then
        mockMvc.perform(get("/api/users/nickname/{nickname}", "johndoe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname").value("johndoe"))
                .andExpect(jsonPath("$.firstName").value("John"));

        verify(userUseCase).getUserByNickname("johndoe");
    }

    @Test
    void testUpdateUser_WithValidData_ShouldReturn200() throws Exception {
        // Given
        UserDTO updateDTO = UserDTO.builder()
                .firstName("John Updated")
                .lastName("Doe Updated")
                .email("john.updated@example.com")
                .nickname("johnupdated")
                .build();

        User updatedUser = User.builder()
                .id(userId)
                .firstName("John Updated")
                .lastName("Doe Updated")
                .email("john.updated@example.com")
                .nickname("johnupdated")
                .build();

        when(userUseCase.updateUser(eq(userId), any(User.class))).thenReturn(updatedUser);

        // When & Then
        mockMvc.perform(put("/api/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John Updated"))
                .andExpect(jsonPath("$.lastName").value("Doe Updated"))
                .andExpect(jsonPath("$.email").value("john.updated@example.com"))
                .andExpect(jsonPath("$.nickname").value("johnupdated"));

        verify(userUseCase).updateUser(eq(userId), any(User.class));
    }

    @Test
    void testUpdateUser_WithNonExistentUser_ShouldReturn404() throws Exception {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        UserDTO updateDTO = UserDTO.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .nickname("johndoe")
                .build();

        when(userUseCase.updateUser(eq(nonExistentId), any(User.class)))
                .thenThrow(new EntityNotFoundException("User", nonExistentId));

        // When & Then
        mockMvc.perform(put("/api/users/{id}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteUser_WithExistingUser_ShouldReturn204() throws Exception {
        // Given
        doNothing().when(userUseCase).deleteUser(userId);

        // When & Then
        mockMvc.perform(delete("/api/users/{id}", userId))
                .andExpect(status().isNoContent());

        verify(userUseCase).deleteUser(userId);
    }

    @Test
    void testDeleteUser_WithNonExistentUser_ShouldReturn404() throws Exception {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        doThrow(new EntityNotFoundException("User", nonExistentId))
                .when(userUseCase).deleteUser(nonExistentId);

        // When & Then
        mockMvc.perform(delete("/api/users/{id}", nonExistentId))
                .andExpect(status().isNotFound());
    }
}
