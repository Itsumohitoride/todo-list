package com.todo.todoList.repository;

import com.todo.todoList.model.TodoList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TodoListRepository extends JpaRepository<TodoList, UUID> { }
