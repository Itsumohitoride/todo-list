package com.todo.todoList.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI (Swagger) configuration for API documentation
 *
 * Access the documentation at:
 * - Swagger UI: http://localhost:8080/swagger-ui.html
 * - OpenAPI JSON: http://localhost:8080/v3/api-docs
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI todoListOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("TodoList API")
                        .version("1.0.0")
                        .description("REST API for TodoList Application - Hexagonal Architecture")
                        .contact(new Contact()
                                .name("TodoList Team")
                                .email("support@todolist.com")));
    }
}
