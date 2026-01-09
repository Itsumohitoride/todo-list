package com.todo.todoList.infrastructure.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Spring Bean Configuration for Hexagonal Architecture
 *
 * This configuration ensures all layers are properly scanned and wired:
 * - Domain: Pure business logic (no Spring dependencies)
 * - Application: Use Cases (@Service)
 * - Infrastructure: Adapters, Repositories, Mappers (@Component, @Repository)
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.todo.todoList.infrastructure.adapter.out.persistence.repository")
@ComponentScan(basePackages = {
        "com.todo.todoList.application.usecase",          // Use Cases
        "com.todo.todoList.infrastructure.adapter",        // Adapters (in/out)
        "com.todo.todoList.infrastructure.mapper",         // Mappers
        "com.todo.todoList.infrastructure.validation",     // Validators
        "com.todo.todoList.infrastructure.exception"       // Exception Handlers
})
public class BeanConfiguration {
    // All beans are auto-discovered via @Component, @Service, @Repository annotations
    // No manual bean creation needed due to proper architecture
}
