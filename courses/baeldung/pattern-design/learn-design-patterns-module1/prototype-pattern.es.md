# Aprende Patrones de Diseño — Módulo 1, Lección 5: El Patrón Prototipo

> Notas resumen de `the-prototype-pattern-start` / `the-prototype-pattern-end` (curso "Learn Design Patterns" de Baeldung).
> English version: [prototype-pattern.md](./prototype-pattern.md)

## 1. Descripción general

El patrón Prototipo es un patrón **creacional** que resuelve un problema específico: crear nuevos objetos copiando objetos existentes, sin acoplar el cliente a la clase concreta que se está copiando.

Módulos:
- Inicio: `the-prototype-pattern-start`
- Fin (solución de referencia): `the-prototype-pattern-end`

## 2. El problema: duplicar objetos complejos

Modelo de dominio:
- `Campaign`: `id`, `name`, `description`, `List<Task> tasks`
- `Task`: `id`, `name`, `description`, `dueDate`, `status` (enum `TaskStatus`)

El `CampaignService.duplicateCampaign()` ingenuo copia campo por campo:

```java
public Campaign duplicateCampaign(Campaign original) {
    Campaign copy = new Campaign();
    copy.setName(original.getName());
    copy.setDescription(original.getDescription());
    for (Task task : original.getTasks()) {
        Task taskCopy = new Task();
        taskCopy.setName(task.getName());
        taskCopy.setDueDate(task.getDueDate());
        taskCopy.setStatus(task.getStatus());
        copy.addTask(taskCopy);
    }
    return copy;
}
```

Tres problemas:
1. **Acoplamiento estrecho a cada campo** de `Campaign` y `Task`. Agregar un campo rompe silenciosamente este método — de hecho ya tiene un bug: no copia el `description` de cada tarea.
2. **Sin soporte de polimorfismo** — si `Task` tuviera subclases, este método necesitaría comprobación de tipos y casting para copiarlas correctamente.
3. **La copia campo por campo no escala** — es frágil y propensa a errores a medida que los objetos se vuelven más complejos.

Prototype resuelve esto trasladando la responsabilidad de copiar **al propio objeto**. El objeto conoce sus propios campos, así que puede copiarlos de forma fiable. El cliente simplemente llama a `copy()`.

## 3. ¿Qué es el patrón Prototipo?

> Permite que un cliente cree nuevos objetos copiando uno existente, sin conocer la clase concreta del objeto copiado.

### 3.1. Participantes (GoF)

| Rol | En este ejemplo |
|---|---|
| **Prototype** | interfaz `Prototype<T>` — declara `copy()` |
| **ConcretePrototype** | `Campaign`, `Task` — implementan `copy()` mediante un constructor de copia interno |
| **Client** | código que llama a `copy()` sin depender de tipos concretos |

Ubicación entre los patrones creacionales: **Factory Method** controla *qué clase* se instancia mediante herencia; **Prototype** controla *cómo se crea una copia* mediante delegación al propio objeto. El cliente nunca necesita conocer la clase concreta — funciona por completo a través de la interfaz `Prototype`.

> **Nota sobre el nombre:** el método se llama `copy()`, no `clone()`, para evitar confusión con `Object.clone()` / `Cloneable` de Java, que son un mecanismo distinto con problemas conocidos. Mismo concepto GoF, un nombre más adecuado para Java.

## 4. Implementación

### 4.1. Paso 1 — El constructor de copia

El mecanismo central del patrón es el **constructor de copia**: toma una instancia de la misma clase y crea un nuevo objeto con el mismo estado. Al residir dentro de la clase, tiene acceso directo a todos los campos, incluidos los privados a los que el código externo no puede acceder.

Esa es la ventaja clave frente al enfoque campo por campo de la Sección 2, que está limitado a lo que exponen públicamente los getters/setters.

Cuando el cliente siempre conoce el tipo concreto, un constructor de copia por sí solo basta. Cuando el cliente necesita copiar objetos **sin** conocer su tipo concreto (por ejemplo, trabajando con una mezcla de tipos a través de una interfaz compartida), necesitamos el desacoplamiento que ofrece la interfaz `Prototype`.

### 4.2. La interfaz `Prototype<T>`

```java
public interface Prototype<T> {
    T copy();
}
```

El parámetro de tipo genérico `T` hace que `copy()` devuelva el tipo correcto sin necesidad de casting.

### 4.3. `Campaign` como prototipo

```java
public class Campaign implements Prototype<Campaign> {
    private Long id;
    private String name;
    private String description;
    private List<Task> tasks;

    // ... constructor, getters y setters existentes ...

    Campaign(Campaign source) {
        this.name = source.name;
        this.description = source.description;
        this.tasks = new ArrayList<>(source.tasks);
    }

    @Override
    public Campaign copy() {
        return new Campaign(this);
    }
}
```

`copy()` delega en el constructor de copia — el cliente llama a `campaign.copy()` y obtiene una nueva campaña sin saber cómo ocurre la duplicación. El constructor de copia es un detalle de implementación oculto tras la interfaz `Prototype`: el constructor gestiona *cómo* se copia el objeto, y la interfaz define el contrato que lo *hace copiable*.

Nótese que el constructor de copia omite deliberadamente el `id` — un duplicado es una entidad nueva y debería tener su propia identidad (normalmente asignada por la capa de persistencia), no heredar la del original.

**Bug:** `new ArrayList<>(source.tasks)` crea una nueva *lista*, pero contiene referencias a los **mismos** objetos `Task`. Ambas listas comparten las mismas instancias de `Task` — modificar una tarea en la copia afecta también al original. Necesitamos una **copia profunda** de la lista de tareas.

### 4.4. Copia profunda — Hacer que `Task` también sea un Prototype

```java
public class Task implements Prototype<Task> {
    private Long id;
    private String name;
    private String description;
    private LocalDate dueDate;
    private TaskStatus status;

    // ... constructor, getters y setters existentes ...

    Task(Task source) {
        this.name = source.name;
        this.description = source.description;
        this.dueDate = source.dueDate;
        this.status = source.status;
    }

    @Override
    public Task copy() {
        return new Task(this);
    }
}
```

`String`, `LocalDate` y `TaskStatus` (un enum) son todos inmutables — no necesitan copia profunda. El desafío de la copia profunda se refiere específicamente a la `List<Task>` mutable dentro de `Campaign`.

Corregimos el constructor de copia de `Campaign` para copiar la lista en profundidad:

```java
Campaign(Campaign source) {
    this.name = source.name;
    this.description = source.description;
    this.tasks = source.tasks.stream()
            .map(Task::copy)
            .collect(Collectors.toList());
}
```

Cada tarea de la lista original se copia individualmente mediante su propio `copy()` — una copia totalmente independiente. Las modificaciones a las tareas de la copia ya no afectan al original.

`CampaignService` se reduce a una línea:

```java
public Campaign duplicateCampaign(Campaign original) {
    return original.copy();
}
```

Comparado con el enfoque ingenuo: el servicio ya no conoce los campos de `Campaign`, no itera sobre las tareas, y no necesitará cambiar si la estructura de `Campaign` evoluciona. Los nuevos campos en `Campaign`/`Task` solo requieren actualizar los constructores de copia — `CampaignService` permanece intacto.

> Nótese que `duplicateCampaign()` sigue aceptando un parámetro `Campaign`, así que el cliente *sí* conoce el tipo concreto en este caso. La Sección 4.6 explica por qué la interfaz `Prototype` importa más allá de este escenario.

### 4.5. Pruebas

```java
class PrototypePatternUnitTest {

    @Test
    void givenCampaign_whenCopy_thenCopyHasSameData() {
        Campaign original = new Campaign("Spring Launch", "Q1 campaign");
        original.addTask(new Task("Write blog post", LocalDate.of(2050, 1, 15), TaskStatus.TO_DO));
        original.addTask(new Task("Send newsletter", LocalDate.of(2050, 1, 20), TaskStatus.TO_DO));

        Campaign copy = original.copy();

        assertEquals(original.getName(), copy.getName());
        assertEquals(original.getDescription(), copy.getDescription());
        assertEquals(original.getTasks().size(), copy.getTasks().size());
    }

    @Test
    void givenCopiedCampaign_whenModifyCopyTask_thenOriginalUnchanged() {
        Campaign original = new Campaign("Spring Launch", "Q1 campaign");
        original.addTask(new Task("Write blog post", LocalDate.of(2050, 1, 15), TaskStatus.TO_DO));

        Campaign copy = original.copy();
        copy.getTasks().get(0).setName("Updated task");

        assertEquals("Write blog post", original.getTasks().get(0).getName());
        assertNotSame(original.getTasks().get(0), copy.getTasks().get(0));
    }
}
```

La segunda prueba es la evidencia de que la copia profunda funciona: `assertNotSame` confirma instancias distintas, y la comparación de nombres confirma que los datos del original se conservan tras modificar la copia.

Ejecutar: `mvn test`.

### 4.6. El valor de la interfaz

En esta implementación, `CampaignService` ya sabe que trabaja con una `Campaign` — la interfaz `Prototype` aún no aporta valor de desacoplamiento. Este valor aparece cuando un método funciona con **cualquier objeto copiable** sin saber qué es:

```java
public <T> T duplicate(Prototype<T> prototype) {
    return prototype.copy();
}
```

Esto puede duplicar una `Campaign`, una `Task`, o cualquier futura implementación de `Prototype`, sin conocer ni preocuparse por el tipo concreto. Ese es el desacoplamiento que persigue el patrón GoF: el cliente opera completamente a través de la interfaz, y se pueden agregar nuevos tipos copiables sin tocar el código del cliente.

El ejemplo de campaña se centra en la mecánica: definir la interfaz, implementar `copy()` mediante un constructor de copia, gestionar las copias profundas. El verdadero potencial del patrón se manifiesta cuando estos componentes se usan de forma polimórfica, como en el método genérico anterior, o mediante un **Registro de Prototipos** (un catálogo de prototipos preconfigurados que se consultan y copian por clave).

### 4.7. Una nota sobre `Cloneable`

Java tiene un mecanismo de clonación integrado: la interfaz de marcador `java.lang.Cloneable` junto con `Object.clone()`.

- **A favor:** integrado en el lenguaje, sin necesidad de una interfaz personalizada.
- **En contra:** `Object.clone()` hace una **copia superficial** por defecto, así que la copia profunda debe codificarse a mano. `Cloneable` es una interfaz de marcador sin declaración del método `clone()` — el método vive en `Object`. El contrato se considera generalmente frágil y propenso a errores.

Nuestro enfoque — una interfaz `Prototype<T>` personalizada con `copy()` — evita estos problemas por completo: es seguro en cuanto a tipos, el contrato es explícito, y no entra en conflicto con `Object.clone()`. La mayoría de los proyectos Java modernos prefieren este enfoque frente a `Cloneable`.

## 5. Cuándo usarlo / cuándo no

### Úsalo cuando
- Crear un nuevo objeto es **costoso o complejo**, y un objeto existente ya tiene la mayor parte del estado deseado (p. ej., evita consultas costosas a la base de datos o cálculos complejos al duplicar un objeto ya inicializado).
- El cliente no debería depender de la clase concreta del objeto a copiar — `Prototype` desacopla al cliente de la jerarquía de tipos concreta.
- El sistema necesita copiar objetos cuyos tipos se determinan **en tiempo de ejecución** — sin comprobaciones de tipo ni casting, solo `copy()`.
- Como alternativa a la proliferación de subclases de fábrica: en vez de una jerarquía `Creator` paralela (Factory Method), se almacenan instancias de prototipo y se copian cuando se necesita un objeto nuevo.

### Evítalo cuando
- Los objetos son simples y baratos de construir — la infraestructura de Prototype (interfaz, constructores de copia) añade complejidad sin beneficio real; una llamada directa al constructor es más clara.
- Los objetos tienen **referencias circulares** o grafos de objetos profundamente anidados — copiarlos en profundidad es propenso a errores y puede causar fallos sutiles.
- Los objetos contienen recursos externos (conexiones a BD, descriptores de archivo) — normalmente estos recursos no deberían duplicarse.
- El cliente ya conoce la clase del objeto y es poco probable que cambie — la ventaja del desacoplamiento no aplica; una copia directa es más simple.

### 5.3. Patrones relacionados

Vale la pena comparar Prototype con Factory Method: ambos desacoplan al cliente de los tipos concretos. Factory Method lo hace mediante **herencia** — cada subclase decide qué clase instanciar. Prototype lo hace mediante **delegación** — el cliente le pide a un objeto existente que se copie a sí mismo.

Esto importa en la práctica cuando el conjunto de tipos concretos es grande: Factory Method necesita una subclase creadora por tipo; Prototype simplemente almacena instancias y las copia bajo demanda.

## 6. Uso en el mundo real

El `ObjectMapper` de Jackson — una de las clases más utilizadas del ecosistema Java — admite un método `copy()` que crea un nuevo mapeador con la misma configuración que el original:

```java
ObjectMapper base = new ObjectMapper()
    .enable(SerializationFeature.INDENT_OUTPUT);

ObjectMapper specialized = base.copy()
    .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
```

Internamente, `copy()` delega en un constructor de copia protegido — exactamente el mismo enfoque usado aquí para `Campaign`. Quien llama obtiene una copia totalmente independiente y puede personalizarla aún más sin afectar al original.

Este es un caso de uso real habitual del concepto de prototipo: partir de un objeto base cuidadosamente configurado y luego generar variantes especializadas copiando y ajustando. La alternativa — repetir la configuración completa para cada variante — es propensa a errores y difícil de mantener.

## 7. Conclusión

| | Copia ingenua campo por campo | Patrón Prototype |
|---|---|---|
| Acoplamiento | Estrecho — el cliente conoce cada campo | Débil — el cliente llama a `copy()` |
| Campos nuevos | Rompe la copia silenciosamente | Solo cambia el constructor de copia |
| Polimorfismo | Necesita comprobaciones de tipo/casting | Funciona de forma transparente vía la interfaz |
| Copia profunda | Manual, en cada punto de llamada | Centralizada en cada `copy()` |

El constructor de copia es el mecanismo interno de `copy()`; el patrón en sí es la interfaz `Prototype` que permite a los clientes copiar objetos sin conocer su tipo concreto. Para colecciones mutables y objetos anidados, cada elemento debe copiarse individualmente para garantizar una verdadera independencia entre el original y la copia.
