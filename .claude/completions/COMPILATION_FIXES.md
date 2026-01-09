# Correcciones de Errores de Compilación

## Fecha: 2026-01-09
## Fase: 1.1 - Completar Mappers + Corrección de Tests

---

## 📋 Resumen de Problemas Corregidos

### 1. **Errores del Modelo Sharing**
- ❌ Interfaz retornaba `Optional<Sharing>` pero tests esperaban `Sharing`
- ❌ Métodos `addUserToSharing` y `removeUserFromSharing` eran void pero tests esperaban Sharing
- ❌ Tests usaban `.listId(UUID)` pero el modelo tiene `.list(TodoList)`

**Archivos corregidos:**
- `src/main/java/com/todo/todoList/domain/port/in/IManageSharingUseCase.java`
- `src/main/java/com/todo/todoList/application/usecase/SharingUseCase.java`
- `src/test/java/com/todo/todoList/domain/model/SharingTest.java`
- `src/test/java/com/todo/todoList/application/usecase/SharingUseCaseTest.java`

---

### 2. **Errores del Modelo TodoList**
- ❌ Campo `listType` usado incorrectamente como `type`
- ❌ Campo `user` (tipo User) usado incorrectamente como `userId` (tipo UUID)
- ❌ `.getType()` → debe ser `.getListType()`
- ❌ `.getUserId()` → debe ser `.getUser().getId()`

**Errores encontrados:**
```
cannot find symbol: method type(com.todo.todoList.domain.enums.ListType)
cannot find symbol: method getUserId()
```

**Archivos corregidos:**
- `src/test/java/com/todo/todoList/domain/model/TodoListTest.java`
- `src/test/java/com/todo/todoList/application/usecase/TodoListUseCaseTest.java`
- `src/test/java/com/todo/todoList/domain/model/UserTest.java`

---

### 3. **Errores del Modelo Task**
- ❌ Campo `description`, no `title`
- ❌ Campo `date`, no `dueDate`
- ❌ Campo `list` (tipo TodoList), no `listId` (tipo UUID)
- ❌ Enum `TaskType` tiene: IMPORTANT, TODAY, FEATURED (no REMINDER ni EVENT)
- ❌ `.getDate()`, no `.getDueDate()`
- ❌ `.getList().getId()`, no `.getListId()`

**Errores encontrados:**
```
cannot find symbol variable REMINDER
cannot find symbol variable EVENT
cannot find symbol method title(java.lang.String)
cannot find symbol method getTitle()
cannot find symbol method getDueDate()
cannot find symbol method getListId()
method createTask cannot be applied to given types
```

**Archivos corregidos:**
- `src/test/java/com/todo/todoList/domain/model/TaskTest.java`
- `src/test/java/com/todo/todoList/application/usecase/TaskUseCaseTest.java`
- `src/test/java/com/todo/todoList/domain/model/TodoListTest.java` (método helper `createTask()`)

---

## 🔧 Cambios Detallados por Archivo

### Sharing - Interfaz y Caso de Uso

**`IManageSharingUseCase.java`:**
```java
// ANTES
Optional<Sharing> getSharingById(UUID id);
void addUserToSharing(UUID sharingId, UUID userId);

// DESPUÉS
Sharing getSharingById(UUID id);
Sharing addUserToSharing(UUID sharingId, UUID userId);
```

**`SharingUseCase.java`:**
```java
// ANTES
public Optional<Sharing> getSharingById(UUID id) {
    return sharingRepository.findById(id);
}

// DESPUÉS
public Sharing getSharingById(UUID id) {
    return sharingRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Sharing", id));
}
```

---

### Sharing - Tests

**`SharingTest.java` - setUp():**
```java
// ANTES
sharing = Sharing.builder()
        .id(sharingId)
        .listId(listId)  // ❌ No existe
        .build();

// DESPUÉS
TodoList todoList = TodoList.builder()
        .id(listId)
        .name("Test List")
        .build();

sharing = Sharing.builder()
        .id(sharingId)
        .list(todoList)  // ✅ Correcto
        .build();
```

**`SharingUseCaseTest.java` - createSharing test:**
```java
// ANTES
when(todoListRepository.existsById(listId)).thenReturn(true);
Sharing result = sharingUseCase.createSharing(testSharing);  // ❌ Pasa objeto

// DESPUÉS
when(todoListRepository.findById(listId)).thenReturn(Optional.of(testTodoList));
Sharing result = sharingUseCase.createSharing(listId);  // ✅ Pasa UUID
```

---

### TodoList - Tests

**`TodoListTest.java` - setUp():**
```java
// ANTES
todoList = TodoList.builder()
        .id(listId)
        .name("My Todo List")
        .type(ListType.PERSONAL)      // ❌ No existe
        .userId(userId)                // ❌ No existe
        .build();

// DESPUÉS
User user = User.builder()
        .id(userId)
        .firstName("Test")
        .lastName("User")
        .email("test@example.com")
        .nickname("testuser")
        .build();

todoList = TodoList.builder()
        .id(listId)
        .name("My Todo List")
        .listType(ListType.PERSONAL)  // ✅ Correcto
        .user(user)                    // ✅ Correcto
        .build();
```

**Acceso a campos:**
```java
// ANTES
result.getType()      // ❌ No existe
result.getUserId()    // ❌ No existe

// DESPUÉS
result.getListType()         // ✅ Correcto
result.getUser().getId()     // ✅ Correcto
```

---

### Task - Modelo y Enum

**Modelo Task tiene:**
- ✅ `description` (String)
- ✅ `date` (LocalDate)
- ✅ `completed` (LocalDate)
- ✅ `list` (TodoList)
- ✅ `status` (Status enum)
- ✅ `type` (TaskType enum)

**TaskType enum tiene:**
```java
public enum TaskType {
    IMPORTANT,  // ✅ Usar este
    TODAY,      // ✅ Usar este
    FEATURED    // ✅ Usar este
    // NO tiene: REMINDER, EVENT
}
```

---

### Task - Tests

**`TaskTest.java` - setUp():**
```java
// ANTES
task = Task.builder()
        .id(taskId)
        .title("Test Task")           // ❌ No existe
        .description("Test Description")
        .type(TaskType.REMINDER)      // ❌ No existe
        .dueDate(LocalDate.now())     // ❌ No existe
        .listId(listId)                // ❌ No existe
        .build();

// DESPUÉS
TodoList todoList = TodoList.builder()
        .id(listId)
        .name("Test List")
        .build();

task = Task.builder()
        .id(taskId)
        .description("Test Description")
        .type(TaskType.IMPORTANT)      // ✅ Correcto
        .date(LocalDate.now())         // ✅ Correcto
        .list(todoList)                // ✅ Correcto
        .build();
```

**Métodos de Task:**
```java
// ANTES
task.setDueDate()    // ❌ No existe
task.getDueDate()    // ❌ No existe
task.getTitle()      // ❌ No existe
task.getListId()     // ❌ No existe

// DESPUÉS
task.setDate()           // ✅ Correcto
task.getDate()           // ✅ Correcto
task.getDescription()    // ✅ Correcto
task.getList().getId()   // ✅ Correcto
```

---

### TaskUseCase - Firma de métodos

**Interfaz `IManageTaskUseCase`:**
```java
// Método createTask requiere 2 parámetros:
Task createTask(Task task, UUID todoListId);

// NO es:
Task createTask(Task task);  // ❌ Incorrecto
```

**Ejemplo de uso en test:**
```java
// ANTES
Task result = taskUseCase.createTask(testTask);  // ❌ Falta parámetro

// DESPUÉS
Task result = taskUseCase.createTask(testTask, listId);  // ✅ Correcto
```

**Métodos que retornan Task directamente:**
```java
// ✅ markAsCompleted retorna Task
Task result = taskUseCase.markAsCompleted(taskId);

// ✅ markAsPending retorna Task
Task result = taskUseCase.markAsPending(taskId);
```

**Método que retorna Optional:**
```java
// ✅ getTaskById retorna Optional<Task>
Optional<Task> result = taskUseCase.getTaskById(taskId);
assertTrue(result.isPresent());
```

---

## 📊 Resumen de Cambios por Tipo

### Cambios en Nombres de Campos:

| Modelo | ❌ Incorrecto | ✅ Correcto |
|--------|--------------|------------|
| TodoList | `.type()` | `.listType()` |
| TodoList | `.userId()` | `.user()` |
| TodoList | `.getType()` | `.getListType()` |
| TodoList | `.getUserId()` | `.getUser().getId()` |
| Task | `.title()` | `.description()` |
| Task | `.dueDate()` | `.date()` |
| Task | `.listId()` | `.list()` |
| Task | `.getTitle()` | `.getDescription()` |
| Task | `.getDueDate()` | `.getDate()` |
| Task | `.getListId()` | `.getList().getId()` |
| Sharing | `.listId()` | `.list()` |

### Cambios en Enums:

| ❌ Valor Incorrecto | ✅ Valor Correcto |
|--------------------|------------------|
| `TaskType.REMINDER` | `TaskType.IMPORTANT` o `TaskType.TODAY` o `TaskType.FEATURED` |
| `TaskType.EVENT` | `TaskType.FEATURED` o `TaskType.IMPORTANT` |

### Cambios en Tipos de Retorno:

| Método | ❌ Antes | ✅ Después |
|--------|---------|-----------|
| `IManageSharingUseCase.getSharingById()` | `Optional<Sharing>` | `Sharing` |
| `IManageSharingUseCase.getSharingByTodoListId()` | `Optional<Sharing>` | `Sharing` |
| `IManageSharingUseCase.addUserToSharing()` | `void` | `Sharing` |
| `IManageSharingUseCase.removeUserFromSharing()` | `void` | `Sharing` |

### Cambios en Firmas de Métodos:

| Método | ❌ Antes | ✅ Después |
|--------|---------|-----------|
| `IManageSharingUseCase.createSharing()` | `createSharing(Sharing)` | `createSharing(UUID todoListId)` |
| `IManageTaskUseCase.createTask()` | `createTask(Task)` | `createTask(Task, UUID todoListId)` |

---

## ✅ Archivos Modificados Totales: 11

### Producción (4 archivos):
1. `src/main/java/com/todo/todoList/domain/port/in/IManageSharingUseCase.java`
2. `src/main/java/com/todo/todoList/application/usecase/SharingUseCase.java`
3. `src/main/java/com/todo/todoList/infrastructure/mapper/SharingMapper.java` (agregado updateJpaEntityFromDomain)
4. (Los otros 3 mappers ya fueron actualizados en FASE 1.1)

### Tests (7 archivos):
1. `src/test/java/com/todo/todoList/domain/model/SharingTest.java`
2. `src/test/java/com/todo/todoList/domain/model/TodoListTest.java`
3. `src/test/java/com/todo/todoList/domain/model/TaskTest.java`
4. `src/test/java/com/todo/todoList/domain/model/UserTest.java`
5. `src/test/java/com/todo/todoList/application/usecase/SharingUseCaseTest.java`
6. `src/test/java/com/todo/todoList/application/usecase/TodoListUseCaseTest.java`
7. `src/test/java/com/todo/todoList/application/usecase/TaskUseCaseTest.java`

---

## 🚀 Próximos Pasos

### Verificar compilación:
```bash
cd D:\Projects\todoList
mvnw clean compile
```

### Ejecutar tests:
```bash
mvnw test
```

**Estado esperado:** ✅ Compilación y tests exitosos sin errores

---

## 📝 Lecciones Aprendidas

1. **Consistencia de nombres**: Verificar que los tests usen los nombres exactos de campos del modelo
2. **Tipos de relaciones**: Entender si un campo es un ID (UUID) o un objeto completo
3. **Enums**: Verificar los valores disponibles en el enum antes de usarlos en tests
4. **Firmas de métodos**: Los tests deben coincidir exactamente con las interfaces
5. **Tipos de retorno**: Optional vs objeto directo - decidir temprano y ser consistente
