package com.todo.todoList.domain.port.in;

import com.todo.todoList.application.dto.SharingDTO;

import java.util.List;
import java.util.UUID;

/**
 * Port (Interface) for Sharing Use Cases - Inbound
 */
public interface IManageSharingUseCase {
    /**
     * Create a sharing for a TodoList
     * Generates a unique share token
     * @param todoListId ID of the list to share
     * @return Sharing DTO with token and link
     */
    SharingDTO createSharing(UUID todoListId);

    /**
     * Get sharing information by ID
     * @param sharingId Sharing ID
     * @return Sharing DTO
     */
    SharingDTO getSharingById(UUID sharingId);

    /**
     * Get sharing information by share token
     * @param shareToken Unique share token
     * @return Sharing DTO
     */
    SharingDTO getSharingByToken(String shareToken);

    /**
     * Get sharing by TodoList ID
     * @param todoListId TodoList ID
     * @return Sharing DTO
     */
    SharingDTO getSharingByTodoListId(UUID todoListId);

    /**
     * Join a shared list using share token
     * @param shareToken Unique share token
     * @param userId User ID joining the list
     * @return Updated sharing DTO
     */
    SharingDTO joinSharedList(String shareToken, UUID userId);

    /**
     * Get all users who have access to a shared list
     * @param todoListId TodoList ID
     * @return List of user IDs
     */
    List<UUID> getSharedUsers(UUID todoListId);

    /**
     * Generate QR code image for a sharing
     * @param shareToken Share token
     * @param width QR code width in pixels
     * @param height QR code height in pixels
     * @return QR code as byte array (PNG image)
     */
    byte[] generateQRCode(String shareToken, int width, int height);

    /**
     * Delete a sharing (revoke access)
     * @param sharingId Sharing ID
     */
    void deleteSharing(UUID sharingId);
}
