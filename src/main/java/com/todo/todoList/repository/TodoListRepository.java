package com.todo.todoList.repository;

import com.todo.todoList.model.TodoList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TodoListRepository extends JpaRepository<TodoList, UUID> { }
