package com.todo.todoList.domain.port.in;

import com.todo.todoList.domain.model.Sharing;
import com.todo.todoList.domain.model.User;

import java.util.List;
import java.util.UUID;

/**
 * Port (Interface) for Sharing Use Cases - Inbound
 */
public interface IManageSharingUseCase {
    Sharing createSharing(UUID todoListId);
    Sharing getSharingById(UUID id);
    Sharing getSharingByTodoListId(UUID todoListId);
    Sharing addUserToSharing(UUID sharingId, UUID userId);
    Sharing removeUserFromSharing(UUID sharingId, UUID userId);
    List<User> getSharedUsers(UUID todoListId);
    List<Sharing> getAllSharings();
    void deleteSharing(UUID id);
}
