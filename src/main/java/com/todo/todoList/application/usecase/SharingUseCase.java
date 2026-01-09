package com.todo.todoList.application.usecase;

import com.todo.todoList.domain.exception.EntityNotFoundException;
import com.todo.todoList.domain.model.Sharing;
import com.todo.todoList.domain.model.TodoList;
import com.todo.todoList.domain.model.User;
import com.todo.todoList.domain.port.in.IManageSharingUseCase;
import com.todo.todoList.domain.port.out.ISharingRepository;
import com.todo.todoList.domain.port.out.ITodoListRepository;
import com.todo.todoList.domain.port.out.IUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Sharing Use Case Implementation - Application Layer
 */
@Service
@Transactional
public class SharingUseCase implements IManageSharingUseCase {

    private final ISharingRepository sharingRepository;
    private final ITodoListRepository todoListRepository;
    private final IUserRepository userRepository;

    public SharingUseCase(ISharingRepository sharingRepository,
                          ITodoListRepository todoListRepository,
                          IUserRepository userRepository) {
        this.sharingRepository = sharingRepository;
        this.todoListRepository = todoListRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Sharing createSharing(UUID todoListId) {
        // Verify todo list exists
        TodoList todoList = todoListRepository.findById(todoListId)
                .orElseThrow(() -> new EntityNotFoundException("TodoList", todoListId));

        // Create sharing
        Sharing sharing = Sharing.builder()
                .list(todoList)
                .build();

        return sharingRepository.save(sharing);
    }

    @Override
    public Sharing getSharingById(UUID id) {
        return sharingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Sharing", id));
    }

    @Override
    public Sharing getSharingByTodoListId(UUID todoListId) {
        return sharingRepository.findByTodoListId(todoListId)
                .orElseThrow(() -> new EntityNotFoundException("Sharing for TodoList", todoListId));
    }

    @Override
    public Sharing addUserToSharing(UUID sharingId, UUID userId) {
        Sharing sharing = sharingRepository.findById(sharingId)
                .orElseThrow(() -> new EntityNotFoundException("Sharing", sharingId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User", userId));

        sharing.addUser(user);
        return sharingRepository.save(sharing);
    }

    @Override
    public Sharing removeUserFromSharing(UUID sharingId, UUID userId) {
        Sharing sharing = sharingRepository.findById(sharingId)
                .orElseThrow(() -> new EntityNotFoundException("Sharing", sharingId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User", userId));

        sharing.removeUser(user);
        return sharingRepository.save(sharing);
    }

    @Override
    public List<Sharing> getAllSharings() {
        return sharingRepository.findAll();
    }

    @Override
    public List<User> getSharedUsers(UUID todoListId) {
        Sharing sharing = sharingRepository.findByTodoListId(todoListId)
                .orElseThrow(() -> new EntityNotFoundException("Sharing for TodoList", todoListId));

        return sharing.getUsers();
    }

    @Override
    public void deleteSharing(UUID id) {
        if (!sharingRepository.existsById(id)) {
            throw new EntityNotFoundException("Sharing", id);
        }
        sharingRepository.deleteById(id);
    }
}
