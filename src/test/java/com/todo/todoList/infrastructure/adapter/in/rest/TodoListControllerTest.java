package com.todo.todoList.infrastructure.adapter.in.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todo.todoList.application.dto.TodoListDTO;
import com.todo.todoList.domain.enums.ListType;
import com.todo.todoList.domain.exception.EntityNotFoundException;
import com.todo.todoList.domain.model.TodoList;
import com.todo.todoList.domain.model.User;
import com.todo.todoList.domain.port.in.IManageTodoListUseCase;
import com.todo.todoList.infrastructure.exception.GlobalExceptionHandler;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for TodoListController
 */
@ExtendWith(MockitoExtension.class)
class TodoListControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private IManageTodoListUseCase todoListUseCase;

    @InjectMocks
    private TodoListController todoListController;

    private UUID listId;
    private UUID userId;
    private TodoList testTodoList;
    private User testUser;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(todoListController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();

        listId = UUID.randomUUID();
        userId = UUID.randomUUID();

        testUser = User.builder()
                .id(userId)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .nickname("johndoe")
                .build();

        testTodoList = TodoList.builder()
                .id(listId)
                .name("My Todo List")
                .color("#FF5733")
                .listType(ListType.PERSONAL)
                .user(testUser)
                .build();
    }

    @Test
    void testCreateTodoList_WithValidData_ShouldReturn201() throws Exception {
        // Given
        TodoListDTO requestDTO = TodoListDTO.builder()
                .name("My Todo List")
                .color("#FF5733")
                .listType(ListType.PERSONAL)
                .userId(userId)
                .build();

        when(todoListUseCase.createTodoList(any(TodoList.class))).thenReturn(testTodoList);

        // When & Then
        mockMvc.perform(post("/api/lists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(listId.toString()))
                .andExpect(jsonPath("$.name").value("My Todo List"))
                .andExpect(jsonPath("$.color").value("#FF5733"))
                .andExpect(jsonPath("$.listType").value("PERSONAL"))
                .andExpect(jsonPath("$.userId").value(userId.toString()));

        verify(todoListUseCase).createTodoList(any(TodoList.class));
    }

    @Test
    void testCreateTodoList_WithMissingName_ShouldReturn400() throws Exception {
        // Given - Missing name
        TodoListDTO requestDTO = TodoListDTO.builder()
                .color("#FF5733")
                .listType(ListType.PERSONAL)
                .userId(userId)
                .build();

        // When & Then
        mockMvc.perform(post("/api/lists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.fieldErrors.name").exists());
    }

    @Test
    void testGetTodoListById_WithExistingList_ShouldReturn200() throws Exception {
        // Given
        when(todoListUseCase.getTodoListById(listId)).thenReturn(testTodoList);

        // When & Then
        mockMvc.perform(get("/api/lists/{id}", listId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(listId.toString()))
                .andExpect(jsonPath("$.name").value("My Todo List"))
                .andExpect(jsonPath("$.color").value("#FF5733"))
                .andExpect(jsonPath("$.listType").value("PERSONAL"))
                .andExpect(jsonPath("$.userId").value(userId.toString()));

        verify(todoListUseCase).getTodoListById(listId);
    }

    @Test
    void testGetTodoListById_WithNonExistentList_ShouldReturn404() throws Exception {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        when(todoListUseCase.getTodoListById(nonExistentId))
                .thenThrow(new EntityNotFoundException("TodoList", nonExistentId));

        // When & Then
        mockMvc.perform(get("/api/lists/{id}", nonExistentId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"));
    }

    @Test
    void testGetAllTodoLists_ShouldReturn200WithList() throws Exception {
        // Given
        TodoList list2 = TodoList.builder()
                .id(UUID.randomUUID())
                .name("Second List")
                .color("#00FF00")
                .listType(ListType.SHARED)
                .user(testUser)
                .build();

        List<TodoList> lists = Arrays.asList(testTodoList, list2);
        when(todoListUseCase.getAllTodoLists()).thenReturn(lists);

        // When & Then
        mockMvc.perform(get("/api/lists"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("My Todo List"))
                .andExpect(jsonPath("$[1].name").value("Second List"));

        verify(todoListUseCase).getAllTodoLists();
    }

    @Test
    void testGetTodoListsByUserId_ShouldReturn200WithList() throws Exception {
        // Given
        List<TodoList> lists = Arrays.asList(testTodoList);
        when(todoListUseCase.getTodoListsByUserId(userId)).thenReturn(lists);

        // When & Then
        mockMvc.perform(get("/api/lists")
                        .param("userId", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("My Todo List"))
                .andExpect(jsonPath("$[0].userId").value(userId.toString()));

        verify(todoListUseCase).getTodoListsByUserId(userId);
        verify(todoListUseCase, never()).getAllTodoLists();
    }

    @Test
    void testUpdateTodoList_WithValidData_ShouldReturn200() throws Exception {
        // Given
        TodoListDTO updateDTO = TodoListDTO.builder()
                .name("Updated List")
                .color("#0000FF")
                .listType(ListType.SHARED)
                .userId(userId)
                .build();

        TodoList updatedList = TodoList.builder()
                .id(listId)
                .name("Updated List")
                .color("#0000FF")
                .listType(ListType.SHARED)
                .user(testUser)
                .build();

        when(todoListUseCase.updateTodoList(eq(listId), any(TodoList.class))).thenReturn(updatedList);

        // When & Then
        mockMvc.perform(put("/api/lists/{id}", listId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated List"))
                .andExpect(jsonPath("$.color").value("#0000FF"))
                .andExpect(jsonPath("$.listType").value("SHARED"));

        verify(todoListUseCase).updateTodoList(eq(listId), any(TodoList.class));
    }

    @Test
    void testUpdateTodoList_WithNonExistentList_ShouldReturn404() throws Exception {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        TodoListDTO updateDTO = TodoListDTO.builder()
                .name("Updated List")
                .color("#0000FF")
                .listType(ListType.PERSONAL)
                .userId(userId)
                .build();

        when(todoListUseCase.updateTodoList(eq(nonExistentId), any(TodoList.class)))
                .thenThrow(new EntityNotFoundException("TodoList", nonExistentId));

        // When & Then
        mockMvc.perform(put("/api/lists/{id}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testChangeColor_WithValidData_ShouldReturn200() throws Exception {
        // Given
        String newColor = "#FFFF00";
        TodoList updatedList = TodoList.builder()
                .id(listId)
                .name("My Todo List")
                .color(newColor)
                .listType(ListType.PERSONAL)
                .user(testUser)
                .build();

        when(todoListUseCase.changeColor(listId, newColor)).thenReturn(updatedList);

        TodoListController.ColorRequest colorRequest = new TodoListController.ColorRequest();
        colorRequest.setColor(newColor);
        String colorJson = objectMapper.writeValueAsString(colorRequest);

        // When & Then
        mockMvc.perform(put("/api/lists/{id}/color", listId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(colorJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.color").value(newColor));

        verify(todoListUseCase).changeColor(listId, newColor);
    }

    @Test
    void testDeleteTodoList_WithExistingList_ShouldReturn204() throws Exception {
        // Given
        doNothing().when(todoListUseCase).deleteTodoList(listId);

        // When & Then
        mockMvc.perform(delete("/api/lists/{id}", listId))
                .andExpect(status().isNoContent());

        verify(todoListUseCase).deleteTodoList(listId);
    }

    @Test
    void testDeleteTodoList_WithNonExistentList_ShouldReturn404() throws Exception {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        doThrow(new EntityNotFoundException("TodoList", nonExistentId))
                .when(todoListUseCase).deleteTodoList(nonExistentId);

        // When & Then
        mockMvc.perform(delete("/api/lists/{id}", nonExistentId))
                .andExpect(status().isNotFound());
    }
}
