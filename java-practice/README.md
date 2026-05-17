# Java Modern Features Practice

A hands-on study project covering modern Java features introduced in **Java 14–21**, organized by topic with progressive examples.

---

## Java Version Coverage

| Feature | Introduced | Status |
|---|---|---|
| Records | Java 16 (stable) | Practiced |
| Sealed Classes | Java 17 (stable) | Practiced |
| Switch Expressions | Java 14 (stable) | Practiced |
| Pattern Matching (`instanceof`) | Java 16 (stable) | Practiced |
| Pattern Matching in `switch` | Java 21 (stable) | Practiced |
| Guarded Patterns (`when`) | Java 21 (stable) | Practiced |

---

## Project Structure

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
│       ├── Shape.java        (sealed interface)
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

## Features Covered

### 1. Records (Java 16)

Records are immutable data classes. The compiler auto-generates `equals()`, `hashCode()`, `toString()`, and accessors.

```java
record Point(int x, int y) {}

var p1 = new Point(3, 4);
System.out.println(p1); // Point[x=3, y=4]
```

**What was practiced:**
- Basic record declaration (`BasicRecordDemo`)
- Compact constructors with validation (`RecordWithValidation`)
- Records implementing interfaces (`RecordsImplementInterface`)

---

### 2. Sealed Classes (Java 17)

Sealed classes restrict which classes can extend or implement them. The compiler knows all possible subtypes, enabling exhaustive `switch` without `default`.

```java
public sealed interface Shape permits Circle, Rectangle, Triangle {
    double area();
    double perimeter();
}
```

**What was practiced:**
- Sealed interface with `permits`
- Subtypes as `record` (Circle, Rectangle, Triangle)
- Exhaustive `switch` over sealed hierarchy (no `default` needed)

---

### 3. Switch Expressions (Java 14+)

Switch evolved from a statement into an expression that returns a value.

```java
// Arrow syntax — no fall-through, no break
String type = switch (day) {
    case "MONDAY", "FRIDAY" -> "Weekday";
    case "SATURDAY", "SUNDAY" -> "Weekend";
    default -> "Unknown";
};

// yield — return a value from a block
String result = switch (day) {
    case "MONDAY" -> {
        System.out.println("Start of week!");
        yield "Weekday - Monday";
    }
    default -> "Other day";
};
```

**What was practiced:**
- Classic switch statement (pre-Java 14)
- Arrow syntax `->` with multiple labels
- `yield` inside block cases
- Switch over `enum` (exhaustive, no `default`)

---

### 4. Pattern Matching — `instanceof` (Java 16)

Eliminates the explicit cast after `instanceof`.

```java
// Old way
if (obj instanceof String) {
    String s = (String) obj;
}

// New way
if (obj instanceof String s) {
    System.out.println(s.toUpperCase());
}
```

---

### 5. Pattern Matching in `switch` (Java 21)

Combines `switch` with type patterns — no manual casting needed.

```java
static String describe(Object obj) {
    return switch (obj) {
        case Integer i -> "Integer: " + i;
        case Double d  -> "Double: " + d;
        case String s  -> "String: " + s;
        case null      -> "null";
        default        -> "Unknown: " + obj.getClass().getSimpleName();
    };
}
```

---

### 6. Guarded Patterns with `when` (Java 21)

Adds a condition to a pattern case — avoids `if` blocks inside cases.

```java
static String classifyNumber(Object obj) {
    return switch (obj) {
        case Integer i when i > 0  -> "Positive: " + i;
        case Integer i when i == 0 -> "Zero";
        case Integer i             -> "Negative: " + i;
        default                    -> "Not a number";
    };
}
```

> Order matters: Java evaluates cases top to bottom and takes the first match.

---

## Key Takeaways

- **Records** replace verbose POJOs with immutable, concise data carriers.
- **Sealed classes** give the compiler a closed type hierarchy — great combined with `switch`.
- **Switch expressions** remove fall-through bugs and allow returning values directly.
- **Pattern matching** eliminates boilerplate casting throughout the codebase.
- **`when` guards** keep conditional logic inside the `case` label, not inside the body.
