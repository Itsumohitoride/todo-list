package com.todo.todoList.infrastructure.security;

import com.todo.todoList.domain.model.User;
import com.todo.todoList.domain.port.out.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Utility class for security-related operations
 */
@Component
@RequiredArgsConstructor
public class SecurityUtils {

    private final IUserRepository userRepository;

    /**
     * Gets the email of the currently authenticated user
     * @return email of the current user, or null if not authenticated
     */
    public String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        return authentication.getName();
    }

    /**
     * Gets the currently authenticated user
     * @return User object of the current user
     * @throws IllegalStateException if no user is authenticated
     */
    public User getCurrentUser() {
        String email = getCurrentUserEmail();

        if (email == null) {
            throw new IllegalStateException("No authenticated user found");
        }

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found in database"));
    }

    /**
     * Gets the ID of the currently authenticated user
     * @return UUID of the current user
     * @throws IllegalStateException if no user is authenticated
     */
    public UUID getCurrentUserId() {
        return getCurrentUser().getId();
    }

    /**
     * Checks if the current user is the owner of a resource
     * @param ownerId the ID of the resource owner
     * @return true if the current user is the owner
     */
    public boolean isCurrentUser(UUID ownerId) {
        try {
            UUID currentUserId = getCurrentUserId();
            return currentUserId.equals(ownerId);
        } catch (IllegalStateException e) {
            return false;
        }
    }

    /**
     * Checks if the current user has a specific role
     * @param role the role to check
     * @return true if the user has the role
     */
    public boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + role));
    }
}
