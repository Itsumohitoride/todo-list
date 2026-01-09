package com.todo.todoList.application.usecase;

import com.todo.todoList.domain.enums.TaskType;
import com.todo.todoList.domain.exception.EntityNotFoundException;
import com.todo.todoList.domain.model.Task;
import com.todo.todoList.domain.model.TodoList;
import com.todo.todoList.domain.port.in.IManageTaskUseCase;
import com.todo.todoList.domain.port.out.ITaskRepository;
import com.todo.todoList.domain.port.out.ITodoListRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
