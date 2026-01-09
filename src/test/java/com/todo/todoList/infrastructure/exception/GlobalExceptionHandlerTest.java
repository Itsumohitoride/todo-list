package com.todo.todoList.infrastructure.exception;

import com.todo.todoList.domain.exception.DomainException;
import com.todo.todoList.domain.exception.DuplicateEntityException;
import com.todo.todoList.domain.exception.EntityNotFoundException;
import com.todo.todoList.domain.exception.InvalidOperationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for GlobalExceptionHandler
 */
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void testHandleEntityNotFoundException_ShouldReturn404() {
        // Given
        UUID userId = UUID.randomUUID();
        EntityNotFoundException exception = new EntityNotFoundException("User", userId);

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleEntityNotFoundException(exception);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().getStatus());
        assertEquals("Not Found", response.getBody().getError());
        assertTrue(response.getBody().getMessage().contains("User"));
        assertTrue(response.getBody().getMessage().contains(userId.toString()));
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void testHandleDuplicateEntityException_ShouldReturn409() {
        // Given
        String email = "duplicate@example.com";
        DuplicateEntityException exception = new DuplicateEntityException("Email", email);

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleDuplicateEntityException(exception);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(409, response.getBody().getStatus());
        assertEquals("Conflict", response.getBody().getError());
        assertTrue(response.getBody().getMessage().contains("Email"));
        assertTrue(response.getBody().getMessage().contains(email));
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void testHandleInvalidOperationException_ShouldReturn400() {
        // Given
        InvalidOperationException exception = new InvalidOperationException("Cannot perform this operation");

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleInvalidOperationException(exception);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Bad Request", response.getBody().getError());
        assertEquals("Cannot perform this operation", response.getBody().getMessage());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void testHandleDomainException_ShouldReturn400() {
        // Given
        DomainException exception = new DomainException("Domain rule violated");

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleDomainException(exception);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Bad Request", response.getBody().getError());
        assertEquals("Domain rule violated", response.getBody().getMessage());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void testHandleIllegalArgumentException_ShouldReturn400() {
        // Given
        IllegalArgumentException exception = new IllegalArgumentException("TodoList must have a user with valid ID");

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleIllegalArgumentException(exception);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Bad Request", response.getBody().getError());
        assertEquals("TodoList must have a user with valid ID", response.getBody().getMessage());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void testHandleValidationExceptions_ShouldReturn400WithFieldErrors() {
        // Given
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError1 = new FieldError("user", "email", "must not be blank");
        FieldError fieldError2 = new FieldError("user", "nickname", "must not be blank");
        when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError1, fieldError2));

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

        // When
        ResponseEntity<ValidationErrorResponse> response = exceptionHandler.handleValidationExceptions(exception);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Bad Request", response.getBody().getError());
        assertEquals("Validation failed", response.getBody().getMessage());
        assertNotNull(response.getBody().getFieldErrors());
        assertEquals(2, response.getBody().getFieldErrors().size());
        assertEquals("must not be blank", response.getBody().getFieldErrors().get("email"));
        assertEquals("must not be blank", response.getBody().getFieldErrors().get("nickname"));
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void testHandleGenericException_ShouldReturn500() {
        // Given
        Exception exception = new RuntimeException("Unexpected error occurred");

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGenericException(exception);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().getStatus());
        assertEquals("Internal Server Error", response.getBody().getError());
        assertTrue(response.getBody().getMessage().contains("unexpected error"));
        assertTrue(response.getBody().getMessage().contains("Unexpected error occurred"));
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void testErrorResponse_BuilderPattern() {
        // Given/When
        ErrorResponse error = ErrorResponse.builder()
                .status(404)
                .error("Not Found")
                .message("User not found")
                .build();

        // Then
        assertNotNull(error);
        assertEquals(404, error.getStatus());
        assertEquals("Not Found", error.getError());
        assertEquals("User not found", error.getMessage());
    }

    @Test
    void testValidationErrorResponse_BuilderPattern() {
        // Given/When
        ValidationErrorResponse response = ValidationErrorResponse.builder()
                .status(400)
                .error("Bad Request")
                .message("Validation failed")
                .fieldErrors(java.util.Map.of("email", "must not be blank"))
                .build();

        // Then
        assertNotNull(response);
        assertEquals(400, response.getStatus());
        assertEquals("Bad Request", response.getError());
        assertEquals("Validation failed", response.getMessage());
        assertEquals(1, response.getFieldErrors().size());
        assertEquals("must not be blank", response.getErrors().get("email"));
    }
}
