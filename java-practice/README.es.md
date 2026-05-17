# Práctica de Features Modernas de Java

Proyecto de estudio práctico sobre las features modernas introducidas en **Java 14–21**, organizado por tema con ejemplos progresivos.

---

## Cobertura por Versión

| Feature | Introducida en | Estado |
|---|---|---|
| Records | Java 16 (estable) | Practicado |
| Sealed Classes | Java 17 (estable) | Practicado |
| Switch Expressions | Java 14 (estable) | Practicado |
| Pattern Matching (`instanceof`) | Java 16 (estable) | Practicado |
| Pattern Matching en `switch` | Java 21 (estable) | Practicado |
| Guarded Patterns (`when`) | Java 21 (estable) | Practicado |

---

## Estructura del Proyecto

```
src/main/java/com/java/practice/
├── records/
│   ├── BasicRecordDemo.java
│   ├── RecordWithValidation.java
│   └── RecordsImplementInterface.java
├── sealed/
│   ├── SealedClasDemo.java
│   ├── SealedWithPatterns.java
│   └── shapes/
│       ├── Shape.java        (interfaz sellada)
│       ├── Circle.java
│       ├── Rectangle.java
│       └── Triangle.java
├── pattern/
│   └── PatternMatching.java
├── expression/
│   └── SwtichExpression.java
└── Main.java
```

---

## Features Cubiertas

### 1. Records (Java 16)

Los records son clases de datos inmutables. El compilador genera automáticamente `equals()`, `hashCode()`, `toString()` y los accessors.

```java
record Point(int x, int y) {}

var p1 = new Point(3, 4);
System.out.println(p1); // Point[x=3, y=4]
```

**Lo que se practicó:**
- Declaración básica de record (`BasicRecordDemo`)
- Constructores compactos con validación (`RecordWithValidation`)
- Records implementando interfaces (`RecordsImplementInterface`)

---

### 2. Sealed Classes (Java 17)

Las clases selladas restringen qué clases pueden extenderlas o implementarlas. El compilador conoce todos los subtipos posibles, permitiendo un `switch` exhaustivo sin necesidad de `default`.

```java
public sealed interface Shape permits Circle, Rectangle, Triangle {
    double area();
    double perimeter();
}
```

**Lo que se practicó:**
- Interfaz sellada con `permits`
- Subtipos definidos como `record` (Circle, Rectangle, Triangle)
- `switch` exhaustivo sobre la jerarquía sellada (sin `default`)

---

### 3. Switch Expressions (Java 14+)

El switch evolucionó de sentencia a expresión que retorna un valor.

```java
// Sintaxis con flecha — sin fall-through, sin break
String tipo = switch (dia) {
    case "MONDAY", "FRIDAY" -> "Día de semana";
    case "SATURDAY", "SUNDAY" -> "Fin de semana";
    default -> "Desconocido";
};

// yield — retornar un valor desde un bloque
String resultado = switch (dia) {
    case "MONDAY" -> {
        System.out.println("Inicio de semana!");
        yield "Lunes";
    }
    default -> "Otro día";
};
```

**Lo que se practicó:**
- Switch statement clásico (pre-Java 14) con fall-through y `break`
- Sintaxis de flecha `->` con múltiples labels por caso
- `yield` dentro de bloques `{}`
- Switch sobre `enum` exhaustivo (sin `default`)

---

### 4. Pattern Matching — `instanceof` (Java 16)

Elimina el cast explícito después del `instanceof`.

```java
// Forma antigua
if (obj instanceof String) {
    String s = (String) obj;
}

// Forma nueva
if (obj instanceof String s) {
    System.out.println(s.toUpperCase());
}
```

---

### 5. Pattern Matching en `switch` (Java 21)

Combina `switch` con patrones de tipo — sin necesidad de casteo manual.

```java
static String describir(Object obj) {
    return switch (obj) {
        case Integer i -> "Entero: " + i;
        case Double d  -> "Double: " + d;
        case String s  -> "Texto: " + s;
        case null      -> "nulo";
        default        -> "Desconocido: " + obj.getClass().getSimpleName();
    };
}
```

---

### 6. Guarded Patterns con `when` (Java 21)

Agrega una condición a un case de pattern — evita poner `if` dentro del cuerpo del case.

```java
static String clasificarNumero(Object obj) {
    return switch (obj) {
        case Integer i when i > 0  -> "Positivo: " + i;
        case Integer i when i == 0 -> "Cero";
        case Integer i             -> "Negativo: " + i;
        default                    -> "No es un número";
    };
}
```

> El orden de los casos importa: Java evalúa de arriba hacia abajo y toma el primero que coincide.

---

## Conceptos Clave

- **Records** reemplazan los POJOs verbosos con clases de datos inmutables y concisas.
- **Sealed classes** le dan al compilador una jerarquía de tipos cerrada — muy útil combinada con `switch`.
- **Switch expressions** eliminan los bugs de fall-through y permiten retornar valores directamente.
- **Pattern matching** elimina el boilerplate del casteo en todo el código.
- **Guards con `when`** mantienen la lógica condicional dentro del label del `case`, no dentro del cuerpo.

---

## Diferencia entre `yield` y `return`

| | `return` | `yield` |
|---|---|---|
| Usado en | Métodos | Bloques `{}` dentro de switch expression |
| Sale de | El método completo | Solo el bloque del case |
| Obligatorio cuando | Siempre en métodos con retorno | Siempre dentro de `case { }` en switch expression |
