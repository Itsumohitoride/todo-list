package com.todo.todoList.application.usecase;

import com.todo.todoList.domain.exception.EntityNotFoundException;
import com.todo.todoList.domain.model.TodoList;
import com.todo.todoList.domain.model.User;
import com.todo.todoList.domain.port.in.IManageTodoListUseCase;
import com.todo.todoList.domain.port.out.ITodoListRepository;
import com.todo.todoList.domain.port.out.IUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * TodoList Use Case Implementation - Application Layer
 */
@Service
@Transactional
public class TodoListUseCase implements IManageTodoListUseCase {

    private final ITodoListRepository todoListRepository;
    private final IUserRepository userRepository;

    public TodoListUseCase(ITodoListRepository todoListRepository, IUserRepository userRepository) {
        this.todoListRepository = todoListRepository;
        this.userRepository = userRepository;
    }

    @Override
    public TodoList createTodoList(TodoList todoList) {
        // Verify user exists
        if (todoList.getUser() == null || todoList.getUser().getId() == null) {
            throw new IllegalArgumentException("TodoList must have a user with valid ID");
        }

        UUID userId = todoList.getUser().getId();
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User", userId);
        }

        return todoListRepository.save(todoList);
    }

    @Override
    public TodoList updateTodoList(UUID id, TodoList todoList) {
        TodoList existing = todoListRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("TodoList", id));

        // Update fields
        existing.setName(todoList.getName());
        existing.setColor(todoList.getColor());
        existing.setListType(todoList.getListType());

        return todoListRepository.save(existing);
    }

    @Override
    public void deleteTodoList(UUID id) {
        if (!todoListRepository.existsById(id)) {
            throw new EntityNotFoundException("TodoList", id);
        }
        todoListRepository.deleteById(id);
    }

    @Override
    public TodoList getTodoListById(UUID id) {
        return todoListRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("TodoList", id));
    }

    @Override
    public List<TodoList> getTodoListsByUserId(UUID userId) {
        // Verify user exists
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User", userId);
        }
        return todoListRepository.findByUserId(userId);
    }

    @Override
    public List<TodoList> getAllTodoLists() {
        return todoListRepository.findAll();
    }

    @Override
    public TodoList changeColor(UUID id, String color) {
        TodoList todoList = todoListRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("TodoList", id));

        todoList.setColor(color);
        return todoListRepository.save(todoList);
    }
}
