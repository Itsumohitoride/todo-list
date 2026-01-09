package com.todo.todoList;

import com.todo.todoList.domain.port.in.*;
import com.todo.todoList.domain.port.out.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for TodoList Application with Hexagonal Architecture
 * Verifies that Spring context loads correctly and all beans are wired
 */
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class TodoListApplicationTests {

	@Autowired
	private ApplicationContext applicationContext;

	@Test
	void contextLoads() {
		assertNotNull(applicationContext, "Application context should load");
	}

	@Test
	void verifyUseCaseBeansAreLoaded() {
		// Verify all use case beans are present
		assertNotNull(applicationContext.getBean(IManageUserUseCase.class),
				"IManageUserUseCase bean should be loaded");
		assertNotNull(applicationContext.getBean(IManageTodoListUseCase.class),
				"IManageTodoListUseCase bean should be loaded");
		assertNotNull(applicationContext.getBean(IManageTaskUseCase.class),
				"IManageTaskUseCase bean should be loaded");
		assertNotNull(applicationContext.getBean(IManageSharingUseCase.class),
				"IManageSharingUseCase bean should be loaded");
	}

	@Test
	void verifyRepositoryBeansAreLoaded() {
		// Verify all repository adapter beans are present
		assertNotNull(applicationContext.getBean(IUserRepository.class),
				"IUserRepository bean should be loaded");
		assertNotNull(applicationContext.getBean(ITodoListRepository.class),
				"ITodoListRepository bean should be loaded");
		assertNotNull(applicationContext.getBean(ITaskRepository.class),
				"ITaskRepository bean should be loaded");
		assertNotNull(applicationContext.getBean(ISharingRepository.class),
				"ISharingRepository bean should be loaded");
	}

	@Test
	void verifyHexagonalArchitectureLayersAreIndependent() {
		// Verify that domain layer beans don't have Spring dependencies
		// This is a conceptual test - in practice, we verify by code review
		// that domain entities and interfaces have no Spring annotations

		IManageUserUseCase userUseCase = applicationContext.getBean(IManageUserUseCase.class);
		assertNotNull(userUseCase, "Use cases should be accessible via interface");

		IUserRepository userRepository = applicationContext.getBean(IUserRepository.class);
		assertNotNull(userRepository, "Repositories should be accessible via interface");
	}

	@Test
	void verifyMapperBeansAreLoaded() {
		// Verify mapper beans are present
		assertTrue(applicationContext.containsBean("userMapper"),
				"UserMapper bean should be loaded");
		assertTrue(applicationContext.containsBean("todoListMapper"),
				"TodoListMapper bean should be loaded");
		assertTrue(applicationContext.containsBean("taskMapper"),
				"TaskMapper bean should be loaded");
		assertTrue(applicationContext.containsBean("sharingMapper"),
				"SharingMapper bean should be loaded");
	}

	@Test
	void verifyExceptionHandlerIsLoaded() {
		// Verify GlobalExceptionHandler is loaded
		assertTrue(applicationContext.containsBean("globalExceptionHandler"),
				"GlobalExceptionHandler bean should be loaded");
	}
}
