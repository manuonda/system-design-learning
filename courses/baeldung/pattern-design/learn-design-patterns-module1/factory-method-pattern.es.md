# Aprende Patrones de Diseño — Módulo 1, Lección 4: El Patrón Factory Method

> Notas resumen de `the-factory-method-pattern-start` / `the-factory-method-pattern-end` (curso "Learn Design Patterns" de Baeldung).
> English version: [factory-method-pattern.md](./factory-method-pattern.md)

## 1. Descripción general

El patrón Factory Method es un patrón **creacional** que resuelve un problema de diseño habitual: desacoplar una clase de los tipos concretos de objetos que necesita crear.

Se cubren dos enfoques relacionados:

- **Simple Factory** — un idioma simple (no es un patrón GoF) que centraliza la creación detrás de un método estático con lógica condicional.
- **Factory Method (GoF)** — el patrón propiamente dicho, donde las subclases deciden qué clase concreta instanciar mediante polimorfismo, sin condicionales.

Módulos:
- Inicio: `the-factory-method-pattern-start`
- Fin (solución de referencia): `the-factory-method-pattern-end`

## 2. El problema: acoplamiento estrecho a clases concretas

Modelo de dominio:
- `Task` (abstracta): `id` (`Long`), `name` (`String`), `status` (`TaskStatus`)
- `PdfExportTask extends Task`: agrega el campo `recipient`
- `CsvExportTask extends Task`: agrega el campo `query`

El `TaskService` ingenuo decide qué instanciar mediante `if/else` sobre un tipo `String`:

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

Dos problemas:
1. `TaskService` está **fuertemente acoplado** a cada subclase concreta de `Task`.
2. **Viola el Principio Abierto/Cerrado**: agregar `XmlExportTask` implica modificar este método.

## 3. ¿Qué es el patrón Factory Method?

> Define una interfaz para crear un objeto, pero permite que las subclases decidan qué clase instanciar.

En lugar de llamar directamente a un constructor, la responsabilidad de creación se delega a subclases que sobrescriben un método de fábrica.

### 3.1. Participantes

| Rol | En este ejemplo |
|---|---|
| **Product** | `Task` (abstracta) |
| **ConcreteProduct** | `PdfExportTask`, `CsvExportTask` |
| **Creator** | `TaskCreator` (abstracta) |
| **ConcreteCreator** | `PdfExportTaskCreator`, `CsvExportTaskCreator` |

## 4. Implementación

### 4.1. Paso 1 — Simple Factory

Extraemos la lógica condicional a una clase dedicada:

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

`TaskService` ahora delega:

```java
Task task = SimpleTaskFactory.createTask("pdf", "Generate quarterly report");
```

Mejora: `TaskService` ya no referencia directamente `PdfExportTask`/`CsvExportTask`. **Pero** la lógica condicional sigue viva dentro de `SimpleTaskFactory`, y agregar un nuevo tipo sigue requiriendo modificar ese `switch`. Simple Factory **no es un patrón GoF** — es un idioma común, y un buen punto de parada cuando el conjunto de tipos es pequeño y estable.

### 4.2. Paso 2 — Factory Method GoF

Reemplazamos la fábrica única + condicional por una **jerarquía de creadores**: cada subclase es responsable de exactamente un tipo de producto, sin condicionales, sin parámetro de tipo.

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

### 4.3. Refactorización de `TaskService` para usar el Creator

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

`TaskService` ahora depende únicamente de la clase abstracta `TaskCreator`. El parámetro de tipo **desapareció por completo** — la subclase de creador *es* la elección del tipo.

Agregar `XmlExportTask` solo requiere:
- una nueva clase `XmlExportTask`
- una nueva clase `XmlExportTaskCreator`

Ninguna clase existente se modifica — cumplimiento real del Principio Abierto/Cerrado.

> **Nota sobre selección en tiempo de ejecución:** este estilo de inyección por constructor funciona cuando el creador se fija en tiempo de configuración (por ejemplo, el contexto de la app o una capa de config lo elige). Si el creador debe elegirse en tiempo de solicitud a partir de una clave de tipo, un enfoque limpio es un registro: `Map<String, TaskCreator>` poblado al inicio y consultado por clave. Esto mantiene la lógica de selección en un solo lugar sin tocar código existente al agregar nuevas entradas.

### 4.4. Principios SOLID en juego

- **Principio Abierto/Cerrado** — el beneficio principal; los tipos nuevos se agregan con clases nuevas, no con ediciones.
- **Principio de Responsabilidad Única** — cada subclase de `TaskCreator` tiene exactamente una razón para cambiar: construir su producto específico.
- **Principio de Inversión de Dependencias** — la columna vertebral estructural. `TaskService` (alto nivel) depende de `TaskCreator`/`Task` (abstracciones), nunca de clases concretas.

Matiz importante: el patrón **no elimina** el condicional — lo *reubica*. El `if`/`switch` que elige un creador concreto debe existir en algún lugar; se traslada a la **capa de composición/configuración**, lejos del servicio que consume los objetos creados. El consumidor solo debería referenciar el tipo abstracto `Task`.

### 4.5. Pruebas

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

Ejecutar: `mvn test`.

## 5. Cuándo usarlo / cuándo no

### Úsalo cuando
- Una clase no puede anticipar qué clase de objetos debe crear — la decisión pertenece a las subclases (común en frameworks/bibliotecas).
- Se espera que el conjunto de tipos de producto **crezca**.
- La lógica de creación varía significativamente por tipo (detalles propios de construcción, dependencias, configuración).

Patrón relacionado: **Abstract Factory** — crea *familias* de objetos relacionados, frente al tipo de producto único del Factory Method.

### Evítalo cuando
- Solo existe un tipo de producto — una llamada directa al constructor es más simple.
- El conjunto de tipos es pequeño, fijo y poco probable que cambie — un `switch` de Simple Factory es más fácil de mantener.
- **Principal contrapartida:** cada tipo de producto nuevo necesita tanto una nueva clase de producto *como* una nueva clase de creador — dos jerarquías paralelas que crecen juntas. Vale la pena en sistemas que agregan tipos con frecuencia; es sobreingeniería en sistemas pequeños y estables.

## 6. Uso en el mundo real (JDK)

### 6.1. `Collection.iterator()`

```java
Collection<String> list = new ArrayList<>();
Iterator<String> iterator = list.iterator();
```

| Rol | Clase |
|---|---|
| Creator | `Collection` |
| ConcreteCreator | `ArrayList` |
| Product | `Iterator` |
| ConcreteProduct | la clase interna `Itr` de `ArrayList` |

El código cliente depende solo de `Collection`/`Iterator`, nunca de `Itr`. Aquí el caso es más contundente: `ArrayList` es un objeto de dominio genuino con propósito propio, y el rol de fábrica es una extensión *natural* de su naturaleza — a diferencia de `PdfExportTaskCreator`, que es un creador dedicado exclusivamente a eso.

### 6.2. `DocumentBuilderFactory.newDocumentBuilder()`

```java
DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
DocumentBuilder builder = factory.newDocumentBuilder();
```

| Rol | Clase |
|---|---|
| Creator | `DocumentBuilderFactory` |
| ConcreteCreator | provista por la biblioteca de parseo XML (p. ej. Xerces) |
| Product | `DocumentBuilder` |
| ConcreteProduct | la implementación provista por el parser |

Ejemplo a nivel de framework: el JDK define las abstracciones y bibliotecas de terceros proveen las implementaciones concretas. Cambiar el parser XML no requiere cambios en el código de la aplicación.

## 7. Conclusión

| | Simple Factory | Factory Method (GoF) |
|---|---|---|
| Lógica de creación | Centralizada, condicional | Distribuida vía herencia/polimorfismo |
| Agregar un tipo nuevo | Editar el `switch` | Solo agregar clases nuevas |
| Cumple Abierto/Cerrado | No | Sí |
| Costo | Bajo (una sola clase) | Jerarquías de clases paralelas |

Elige Simple Factory para conjuntos de tipos pequeños y estables; pasa a Factory Method cuando se espera que la jerarquía de productos crezca y el cumplimiento del Principio Abierto/Cerrado importe.
