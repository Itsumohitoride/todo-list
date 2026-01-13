package com.todo.todoList.application.usecase;

import com.todo.todoList.application.dto.SharingDTO;
import com.todo.todoList.domain.exception.DuplicateEntityException;
import com.todo.todoList.domain.exception.EntityNotFoundException;
import com.todo.todoList.domain.model.Sharing;
import com.todo.todoList.domain.model.TodoList;
import com.todo.todoList.domain.model.User;
import com.todo.todoList.domain.port.in.IManageSharingUseCase;
import com.todo.todoList.domain.port.out.ISharingRepository;
import com.todo.todoList.domain.port.out.ITodoListRepository;
import com.todo.todoList.domain.port.out.IUserRepository;
import com.todo.todoList.infrastructure.util.QRCodeGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Sharing Use Case Implementation - Application Layer
 */
@Service
@Transactional
public class SharingUseCase implements IManageSharingUseCase {

    private final ISharingRepository sharingRepository;
    private final ITodoListRepository todoListRepository;
    private final IUserRepository userRepository;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    public SharingUseCase(ISharingRepository sharingRepository,
                          ITodoListRepository todoListRepository,
                          IUserRepository userRepository) {
        this.sharingRepository = sharingRepository;
        this.todoListRepository = todoListRepository;
        this.userRepository = userRepository;
    }

    @Override
    public SharingDTO createSharing(UUID todoListId) {
        // Verify todo list exists
        TodoList todoList = todoListRepository.findById(todoListId)
                .orElseThrow(() -> new EntityNotFoundException("TodoList", todoListId));

        // Check if sharing already exists for this list
        if (sharingRepository.findByTodoListId(todoListId).isPresent()) {
            throw new DuplicateEntityException("Sharing already exists for this TodoList", todoListId.toString());
        }

        // Generate unique share token
        String shareToken = UUID.randomUUID().toString();

        // Create sharing
        Sharing sharing = Sharing.builder()
                .shareToken(shareToken)
                .list(todoList)
                .createdAt(LocalDateTime.now())
                .build();

        Sharing saved = sharingRepository.save(sharing);
        return toDTO(saved);
    }

    @Override
    public SharingDTO getSharingById(UUID sharingId) {
        Sharing sharing = sharingRepository.findById(sharingId)
                .orElseThrow(() -> new EntityNotFoundException("Sharing", sharingId));
        return toDTO(sharing);
    }

    @Override
    public SharingDTO getSharingByToken(String shareToken) {
        Sharing sharing = sharingRepository.findByShareToken(shareToken)
                .orElseThrow(() -> new EntityNotFoundException("Sharing with token " + shareToken + " not found"));
        return toDTO(sharing);
    }

    @Override
    public SharingDTO getSharingByTodoListId(UUID todoListId) {
        Sharing sharing = sharingRepository.findByTodoListId(todoListId)
                .orElseThrow(() -> new EntityNotFoundException("Sharing for TodoList", todoListId));
        return toDTO(sharing);
    }

    @Override
    public SharingDTO joinSharedList(String shareToken, UUID userId) {
        // Find sharing by token
        Sharing sharing = sharingRepository.findByShareToken(shareToken)
                .orElseThrow(() -> new EntityNotFoundException("Sharing with token " + shareToken + " not found"));

        // Find user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User", userId));

        // Check if user is already in the sharing
        if (sharing.hasUser(user)) {
            throw new DuplicateEntityException("User already has access to this shared list", userId.toString());
        }

        // Add user to sharing
        sharing.addUser(user);
        Sharing updated = sharingRepository.save(sharing);

        return toDTO(updated);
    }

    @Override
    public List<UUID> getSharedUsers(UUID todoListId) {
        Sharing sharing = sharingRepository.findByTodoListId(todoListId)
                .orElseThrow(() -> new EntityNotFoundException("Sharing for TodoList", todoListId));

        return sharing.getUsers().stream()
                .map(User::getId)
                .collect(Collectors.toList());
    }

    @Override
    public byte[] generateQRCode(String shareToken, int width, int height) {
        // Verify sharing exists
        if (!sharingRepository.existsByShareToken(shareToken)) {
            throw new EntityNotFoundException("Sharing with token " + shareToken + " not found");
        }

        // Generate shareable link
        String shareableLink = generateShareableLink(shareToken);

        // Generate QR code for the link
        return QRCodeGenerator.generateQRCodeImage(shareableLink, width, height);
    }

    @Override
    public void deleteSharing(UUID sharingId) {
        if (!sharingRepository.existsById(sharingId)) {
            throw new EntityNotFoundException("Sharing", sharingId);
        }
        sharingRepository.deleteById(sharingId);
    }

    // ====================================
    // Private Helper Methods
    // ====================================

    private SharingDTO toDTO(Sharing sharing) {
        if (sharing == null) return null;

        List<UUID> userIds = sharing.getUsers() != null
                ? sharing.getUsers().stream().map(User::getId).collect(Collectors.toList())
                : List.of();

        return SharingDTO.builder()
                .id(sharing.getId())
                .shareToken(sharing.getShareToken())
                .todoListId(sharing.getList() != null ? sharing.getList().getId() : null)
                .createdAt(sharing.getCreatedAt())
                .sharedUserIds(userIds)
                .shareableLink(generateShareableLink(sharing.getShareToken()))
                .build();
    }

    private String generateShareableLink(String shareToken) {
        return baseUrl + "/api/sharing/join/" + shareToken;
    }
}
