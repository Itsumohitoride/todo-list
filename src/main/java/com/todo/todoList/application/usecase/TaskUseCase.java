package com.todo.todoList.application.usecase;

import com.todo.todoList.domain.enums.Status;
import com.todo.todoList.domain.enums.TaskType;
import com.todo.todoList.domain.exception.EntityNotFoundException;
import com.todo.todoList.domain.model.Task;
import com.todo.todoList.domain.model.TodoList;
import com.todo.todoList.domain.port.in.IManageTaskUseCase;
import com.todo.todoList.domain.port.out.ITaskRepository;
import com.todo.todoList.domain.port.out.ITodoListRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Task Use Case Implementation - Application Layer
 */
@Service
@Transactional
public class TaskUseCase implements IManageTaskUseCase {

    private final ITaskRepository taskRepository;
    private final ITodoListRepository todoListRepository;

    public TaskUseCase(ITaskRepository taskRepository, ITodoListRepository todoListRepository) {
        this.taskRepository = taskRepository;
        this.todoListRepository = todoListRepository;
    }

    @Override
    public Task createTask(Task task, UUID todoListId) {
        // Verify todo list exists
        TodoList todoList = todoListRepository.findById(todoListId)
                .orElseThrow(() -> new EntityNotFoundException("TodoList", todoListId));

        // Set todo list
        task.setList(todoList);

        // Initialize status as PENDING for new tasks
        task.markAsPending();

        return taskRepository.save(task);
    }

    @Override
    public Task updateTask(UUID id, Task task) {
        Task existing = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task", id));

        // Update fields
        existing.setDescription(task.getDescription());
        existing.setType(task.getType());
        existing.setDate(task.getDate());

        return taskRepository.save(existing);
    }

    @Override
    public void deleteTask(UUID id) {
        if (!taskRepository.existsById(id)) {
            throw new EntityNotFoundException("Task", id);
        }
        taskRepository.deleteById(id);
    }

    @Override
    public Optional<Task> getTaskById(UUID id) {
        return taskRepository.findById(id);
    }

    @Override
    public List<Task> getTasksByTodoListId(UUID todoListId) {
        // Verify todo list exists
        if (!todoListRepository.existsById(todoListId)) {
            throw new EntityNotFoundException("TodoList", todoListId);
        }
        return taskRepository.findByTodoListId(todoListId);
    }

    @Override
    public List<Task> getTasksByType(TaskType type) {
        return taskRepository.findByType(type);
    }

    @Override
    public Task markAsCompleted(UUID id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task", id));

        task.markAsCompleted();
        return taskRepository.save(task);
    }

    @Override
    public Task markAsPending(UUID id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task", id));

        task.markAsPending();
        return taskRepository.save(task);
    }

    @Override
    public List<Task> searchByDescription(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            throw new IllegalArgumentException("Search term cannot be empty");
        }
        return taskRepository.searchByDescriptionContaining(searchTerm);
    }

    @Override
    public List<Task> searchByTodoListIdAndDescription(UUID todoListId, String searchTerm) {
        if (todoListId == null) {
            throw new IllegalArgumentException("TodoList ID cannot be null");
        }
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            throw new IllegalArgumentException("Search term cannot be empty");
        }
        if (!todoListRepository.existsById(todoListId)) {
            throw new EntityNotFoundException("TodoList", todoListId);
        }
        return taskRepository.searchByTodoListIdAndDescriptionContaining(todoListId, searchTerm);
    }

    @Override
    public List<Task> filterByTodoListIdAndStatus(UUID todoListId, Status status) {
        if (todoListId == null) {
            throw new IllegalArgumentException("TodoList ID cannot be null");
        }
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        if (!todoListRepository.existsById(todoListId)) {
            throw new EntityNotFoundException("TodoList", todoListId);
        }
        return taskRepository.findByTodoListIdAndStatus(todoListId, status);
    }

    @Override
    public List<Task> filterByTodoListIdAndDateRange(UUID todoListId, LocalDate startDate, LocalDate endDate) {
        if (todoListId == null) {
            throw new IllegalArgumentException("TodoList ID cannot be null");
        }
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Start date and end date cannot be null");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be before or equal to end date");
        }
        if (!todoListRepository.existsById(todoListId)) {
            throw new EntityNotFoundException("TodoList", todoListId);
        }
        return taskRepository.findByTodoListIdAndDateBetween(todoListId, startDate, endDate);
    }

    @Override
    public List<Task> getOverdueTasks(UUID todoListId) {
        if (todoListId == null) {
            throw new IllegalArgumentException("TodoList ID cannot be null");
        }
        if (!todoListRepository.existsById(todoListId)) {
            throw new EntityNotFoundException("TodoList", todoListId);
        }
        return taskRepository.findOverdueTasks(todoListId);
    }
}
