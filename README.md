# TodoList API

[![Java](https://img.shields.io/badge/Java-17-ED8B00?logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.0-6DB33F?logo=spring-boot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Spring Security](https://img.shields.io/badge/Spring%20Security-6DB33F?logo=spring-security&logoColor=white)](https://spring.io/projects/spring-security)
[![Spring Data JPA](https://img.shields.io/badge/Spring%20Data%20JPA-6DB33F?logo=spring&logoColor=white)](https://spring.io/projects/spring-data-jpa)
[![H2 Database](https://img.shields.io/badge/H2%20Database-0000BB?logo=h2&logoColor=white)](https://www.h2database.com/)
[![MySQL](https://img.shields.io/badge/MySQL-4479A1?logo=mysql&logoColor=white)](https://www.mysql.com/)
[![JWT](https://img.shields.io/badge/JWT-000000?logo=json-web-tokens&logoColor=white)](https://jwt.io/)
[![Lombok](https://img.shields.io/badge/Lombok-BC4521?logo=lombok&logoColor=white)](https://projectlombok.org/)
[![Maven](https://img.shields.io/badge/Maven-C71A36?logo=apache-maven&logoColor=white)](https://maven.apache.org/)
[![Swagger](https://img.shields.io/badge/Swagger-85EA2D?logo=swagger&logoColor=black)](https://swagger.io/)
[![ZXing](https://img.shields.io/badge/ZXing-QR%20Code-orange)](https://github.com/zxing/zxing)
[![Ask DeepWiki](https://deepwiki.com/badge.svg)](https://deepwiki.com/Itsumohitoride/todo-list)

A modern REST API application for task management built with Spring Boot 4.0.0, following hexagonal architecture (ports and adapters).

## Features

- Complete user management with JWT authentication
- Todo list creation and management
- Task management with different states and types
- List sharing system with QR codes
- Statistics and progress charts
- Custom validation with unique fields
- Interactive API documentation with Swagger/OpenAPI
- H2 in-memory database for development

## Technologies

- **Java 17**
- **Spring Boot 4.0.0**
- **Spring Security** with JWT for authentication
- **Spring Data JPA** for persistence
- **H2 Database** (development) / **MySQL** (production)
- **Lombok** for boilerplate reduction
- **SpringDoc OpenAPI** for API documentation
- **ZXing** for QR code generation
- **Maven** as build tool

## Architecture

The project follows **hexagonal architecture** (ports and adapters) with clear separation of concerns:

```
📦 com.todo.todoList
├── 📂 domain/              # Domain layer (business logic)
│   ├── model/              # Domain entities
│   ├── port/
│   │   ├── in/             # Use cases (interfaces)
│   │   └── out/            # Output ports (repository interfaces)
│   ├── enums/              # Domain enumerations
│   └── exception/          # Domain exceptions
├── 📂 application/         # Application layer
│   ├── usecase/            # Use case implementations
│   └── dto/                # Data transfer objects
├── 📂 infrastructure/      # Infrastructure layer
│   ├── adapter/
│   │   ├── in/rest/        # REST controllers
│   │   └── out/persistence/# Persistence adapters
│   ├── config/             # Spring configuration
│   ├── security/           # Security configuration
│   ├── mapper/             # Entity-DTO mappers
│   ├── validation/         # Custom validations
│   ├── exception/          # Global exception handling
│   └── util/               # Utilities
└── TodoListApplication.java
```

### Data Flow

```
REST Controller → Use Case → Domain Port → Repository Adapter → JPA Entity → Database
      ↓              ↓
    DTO    →    Mapper    →    Domain Model
```

## Prerequisites

- Java 17 or higher
- Maven 3.6+ (or use included Maven wrapper)

## Installation

1. Clone the repository:
```bash
git clone <repository-url>
cd todoList
```

2. Build the project:
```bash
./mvnw clean install
```

## Running

### Development mode

```bash
./mvnw spring-boot:run
```

The application will be available at `http://localhost:8080`

### Generate executable JAR

```bash
./mvnw clean package
java -jar target/todoList-0.0.1-SNAPSHOT.jar
```

## Database Access

### H2 Console (Development)

- **URL**: http://localhost:8080/h2-console
- **JDBC URL**: `jdbc:h2:mem:superdb`
- **Username**: `todolist`
- **Password**: _(empty)_

## API Documentation

### Swagger UI

Access the interactive API documentation at:

```
http://localhost:8080/swagger-ui.html
```

### OpenAPI Spec

OpenAPI specification in JSON format:

```
http://localhost:8080/v3/api-docs
```

## Main Endpoints

### Authentication

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register new user |
| POST | `/api/auth/login` | Login |

### Users

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/users/{id}` | Get user by ID |
| GET | `/api/users` | List all users |
| PUT | `/api/users/{id}` | Update user |
| DELETE | `/api/users/{id}` | Delete user |

### Todo Lists

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/todolists` | Create new list |
| GET | `/api/todolists/{id}` | Get list by ID |
| GET | `/api/todolists` | List all user's lists |
| PUT | `/api/todolists/{id}` | Update list |
| DELETE | `/api/todolists/{id}` | Delete list |

### Tasks

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/tasks` | Create new task |
| GET | `/api/tasks/{id}` | Get task by ID |
| GET | `/api/tasks` | List all tasks |
| PUT | `/api/tasks/{id}` | Update task |
| DELETE | `/api/tasks/{id}` | Delete task |

### Sharing

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/sharing` | Create list sharing |
| GET | `/api/sharing/{id}` | Get sharing by ID |
| GET | `/api/sharing/qrcode/{sharingId}` | Generate QR code |

### Statistics

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/statistics` | Get user statistics |

## Testing

### Run all tests

```bash
./mvnw test
```

### Run specific test class

```bash
./mvnw test -Dtest=UserUseCaseTest
```

### Run with coverage

```bash
./mvnw clean test
```

## Data Model

### Main Entities

**User**
- UUID id
- String email (unique)
- String password (encrypted)
- String nickName (unique)
- Role role (ADMIN, USER)
- OneToMany relationship with TodoList

**TodoList**
- UUID id
- String name
- String description
- ListType listType
- LocalDateTime creationDate
- ManyToOne relationship with User
- OneToMany relationship with Task

**Task**
- UUID id
- String title
- String description
- Status status (PENDING, IN_PROGRESS, COMPLETED)
- TaskType taskType (PERSONAL, WORK, SHOPPING, OTHER)
- LocalDateTime creationDate
- LocalDateTime dueDate
- ManyToOne relationship with TodoList

**Sharing**
- UUID id
- String sharedUrl
- OneToOne relationship with TodoList
- ManyToMany relationship with User

## Custom Validations

The project includes a custom validation system with the `@UniqueField` annotation:

```java
@UniqueField(fieldType = UniqueType.EMAIL)
private String email;

@UniqueField(fieldType = UniqueType.NICKNAME)
private String nickName;
```

## Security

- JWT-based authentication
- BCrypt encrypted passwords
- Token validation on each request
- Public endpoints: `/api/auth/**`, `/h2-console/**`, `/swagger-ui/**`, `/v3/api-docs/**`
- Protected endpoints: all others require authentication

## Configuration

### Environment Variables

You can configure the following variables in `application.properties`:

```properties
# Server
server.port=8080

# Database
spring.datasource.url=jdbc:h2:mem:superdb
spring.datasource.username=todolist
spring.datasource.password=

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# JWT
jwt.secret=your-secret-key-here
jwt.expiration=86400000

# H2 Console
spring.h2.console.enabled=true
```

## Useful Maven Commands

```bash
# Clean build artifacts
./mvnw clean

# Compile without running tests
./mvnw clean install -DskipTests

# Package the application
./mvnw package

# View dependencies
./mvnw dependency:tree

# Update dependencies
./mvnw versions:display-dependency-updates
```

## Project Structure

```
todoList/
├── src/
│   ├── main/
│   │   ├── java/com/todo/todoList/
│   │   └── resources/
│   │       ├── application.properties
│   │       └── static/
│   └── test/
│       └── java/com/todo/todoList/
├── .claude/
│   └── CLAUDE.md               # Guide for Claude Code
├── pom.xml
└── README.md
```

## Contributing

1. Fork the project
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## Code Conventions

- Follow standard Java conventions
- Use Lombok to reduce boilerplate
- Document public methods with Javadoc
- Maintain layer separation according to hexagonal architecture
- Write unit tests for new features

## Roadmap

- [ ] Implement notifications
- [ ] Add advanced search filters
- [ ] Tag system for tasks
- [ ] Data export (PDF, CSV)
- [ ] Integration with external calendars
- [ ] Webhooks API

## Contact

For questions or suggestions, please open an issue in the repository.

---

Built with Spring Boot 4.0.0
