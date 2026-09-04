# Learn Design Patterns — Module 1, Lesson 4: The Factory Method Pattern

> Summary notes for `the-factory-method-pattern-start` / `the-factory-method-pattern-end` (Baeldung "Learn Design Patterns" course).
> Versión en español: [factory-method-pattern.es.md](./factory-method-pattern.es.md)

## 1. Overview

The Factory Method pattern is a **creational** pattern that solves a common design problem: decoupling a class from the concrete types of objects it needs to create.

Two related approaches are covered:

- **Simple Factory** — a plain idiom (not a GoF pattern) that centralizes creation behind a static method with conditional logic.
- **Factory Method (GoF)** — the real pattern, where subclasses decide which concrete class to instantiate through polymorphism instead of conditionals.

Modules:
- Start: `the-factory-method-pattern-start`
- End (reference solution): `the-factory-method-pattern-end`

## 2. The Problem: Tight Coupling to Concrete Classes

Domain model:
- `Task` (abstract): `id` (`Long`), `name` (`String`), `status` (`TaskStatus`)
- `PdfExportTask extends Task`: adds a `recipient` field
- `CsvExportTask extends Task`: adds a `query` field

The naive `TaskService` decides what to instantiate using `if/else` on a string type:

```java
public class TaskService {

    public Task createTask(String type, String name) {
        if ("pdf".equals(type)) {
            return new PdfExportTask(name);
        } else if ("csv".equals(type)) {
            return new CsvExportTask(name);
        }
        throw new IllegalArgumentException("Unknown task type: " + type);
    }
}
```

Two problems:
1. `TaskService` is **tightly coupled** to every concrete `Task` subclass.
2. It **violates the Open/Closed Principle** — adding `XmlExportTask` means editing this method.

## 3. What Is the Factory Method Pattern?

> Defines an interface for creating an object, but lets subclasses decide which class to instantiate.

Instead of calling a constructor directly, the creation responsibility is delegated to subclasses that override a factory method.

### 3.1. Participants

| Role | In this example |
|---|---|
| **Product** | `Task` (abstract) |
| **ConcreteProduct** | `PdfExportTask`, `CsvExportTask` |
| **Creator** | `TaskCreator` (abstract) |
| **ConcreteCreator** | `PdfExportTaskCreator`, `CsvExportTaskCreator` |

## 4. Implementation

### 4.1. Step 1 — Simple Factory

Extract the conditional logic into a dedicated class:

```java
public class SimpleTaskFactory {

    public static Task createTask(String type, String name) {
        return switch (type) {
            case "pdf" -> new PdfExportTask(name);
            case "csv" -> new CsvExportTask(name);
            default -> throw new IllegalArgumentException("Unknown task type: " + type);
        };
    }
}
```

`TaskService` now delegates:

```java
Task task = SimpleTaskFactory.createTask("pdf", "Generate quarterly report");
```

Improvement: `TaskService` no longer references `PdfExportTask`/`CsvExportTask` directly. **But** the conditional logic still exists inside `SimpleTaskFactory`, and adding a new type still means editing that `switch`. Simple Factory is **not a GoF pattern** — it's a common idiom, and a fine stopping point when the type set is small and stable.

### 4.2. Step 2 — GoF Factory Method

Replace the single factory + conditional with a **creator hierarchy**: each subclass is responsible for exactly one product type, no conditionals, no type parameter.

```java
public abstract class TaskCreator {
    public abstract Task createTask(String name);
}
```

```java
public class PdfExportTaskCreator extends TaskCreator {
    @Override
    public Task createTask(String name) {
        return new PdfExportTask(name);
    }
}
```

```java
public class CsvExportTaskCreator extends TaskCreator {
    @Override
    public Task createTask(String name) {
        return new CsvExportTask(name);
    }
}
```

### 4.3. Refactoring `TaskService` to Use the Creator

```java
public class TaskService {

    private final TaskCreator taskCreator;

    public TaskService(TaskCreator taskCreator) {
        this.taskCreator = taskCreator;
    }

    public Task createTask(String name) {
        return taskCreator.createTask(name);
    }
}
```

`TaskService` now depends only on the abstract `TaskCreator`. The type parameter is **gone entirely** — the creator subclass *is* the type choice.

Adding `XmlExportTask` requires only:
- a new `XmlExportTask` class
- a new `XmlExportTaskCreator` class

No existing class is modified — real Open/Closed Principle compliance.

> **Runtime selection note:** this constructor-injection style works when the creator is fixed at wiring time (e.g., app context / config layer picks it). If the creator must be chosen at request time from a string key, a clean approach is a registry: `Map<String, TaskCreator>` populated at startup, looked up by key. This keeps the selection logic in one place without touching existing code when new entries are added.

### 4.4. SOLID Principles at Play

- **Open/Closed Principle** — the headline benefit; new types via new classes, not edits.
- **Single Responsibility Principle** — each `TaskCreator` subclass has exactly one reason to change: building its specific product.
- **Dependency Inversion Principle** — the structural backbone. `TaskService` (high-level) depends on `TaskCreator`/`Task` (abstractions), never on concrete classes.

Important nuance: the pattern **does not eliminate** the conditional — it *relocates* it. The `if`/`switch` that picks a concrete creator must exist somewhere; it moves to the **composition/wiring layer**, away from the service that consumes the created objects. The consumer should only ever reference the abstract `Task` type.

### 4.5. Testing

```java
class FactoryMethodPatternUnitTest {

    @Test
    void givenPdfExportTaskCreator_whenCreateTask_thenReturnsPdfExportTask() {
        TaskService service = new TaskService(new PdfExportTaskCreator());

        Task task = service.createTask("Generate quarterly report");

        assertInstanceOf(PdfExportTask.class, task);
        assertEquals("Generate quarterly report", task.getName());
    }

    @Test
    void givenCsvExportTaskCreator_whenCreateTask_thenReturnsCsvExportTask() {
        TaskService service = new TaskService(new CsvExportTaskCreator());

        Task task = service.createTask("Export user data");

        assertInstanceOf(CsvExportTask.class, task);
        assertEquals("Export user data", task.getName());
    }
}
```

Run: `mvn test`.

## 5. When to Use / When Not to Use

### Use it when
- A class can't anticipate which class of objects it must create — the decision belongs to subclasses (common in frameworks/libraries).
- The set of product types is expected to **grow**.
- Creation logic varies meaningfully per type (own construction details, dependencies, configuration).

Related pattern: **Abstract Factory** — creates *families* of related objects, vs. Factory Method's single product type.

### Avoid it when
- Only one product type exists — a direct constructor call is simpler.
- The type set is small, fixed, and unlikely to change — a Simple Factory `switch` is easier to maintain.
- **Main trade-off:** every new product type needs both a new product class *and* a new creator class — two parallel hierarchies growing together. Worth it for systems that add types frequently; overkill for small, stable ones.

## 6. Real-World Usage in the JDK

### 6.1. `Collection.iterator()`

```java
Collection<String> list = new ArrayList<>();
Iterator<String> iterator = list.iterator();
```

| Role | Class |
|---|---|
| Creator | `Collection` |
| ConcreteCreator | `ArrayList` |
| Product | `Iterator` |
| ConcreteProduct | `ArrayList`'s internal `Itr` |

Client code depends only on `Collection`/`Iterator`, never on `Itr`. Note the more compelling case here: `ArrayList` is a genuine domain object with its own purpose, and the factory role is a *natural* extension of it — unlike `PdfExportTaskCreator`, which is a creator dedicated to nothing else.

### 6.2. `DocumentBuilderFactory.newDocumentBuilder()`

```java
DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
DocumentBuilder builder = factory.newDocumentBuilder();
```

| Role | Class |
|---|---|
| Creator | `DocumentBuilderFactory` |
| ConcreteCreator | provided by the XML parser library (e.g., Xerces) |
| Product | `DocumentBuilder` |
| ConcreteProduct | the parser-provided implementation |

Framework-level example: the JDK defines the abstractions, third-party libraries supply the concrete implementations. Swapping the XML parser requires no application code changes.

## 7. Conclusion

| | Simple Factory | Factory Method (GoF) |
|---|---|---|
| Creation logic | Centralized, conditional | Distributed via inheritance/polymorphism |
| Adding a new type | Edit the `switch` | Add new classes only |
| Open/Closed compliant | No | Yes |
| Cost | Low (single class) | Parallel class hierarchies |

Pick Simple Factory for small, stable type sets; graduate to Factory Method when the product hierarchy is expected to grow and Open/Closed compliance matters.
