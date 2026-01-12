# Contexto del Proyecto: Sistema de Biblioteca con DDD

## Objetivo
Crear un sistema de gestión de biblioteca aplicando **Domain-Driven Design (DDD)** y **Arquitectura Hexagonal** usando Spring Boot.

## Propósito
Este proyecto es para **aprender y practicar** los conceptos de DDD basándome en el proyecto de referencia `meetup4j-modulith-ddd-ha` del repositorio `spring-boot-application-architecture-patterns`.

---

## Arquitectura a Aplicar

### Patrones DDD
- ✅ **Aggregates** (Aggregate Roots)
- ✅ **Value Objects** (objetos inmutables con validación)
- ✅ **Domain Events** (comunicación entre bounded contexts)
- ✅ **Repository Pattern** (interfaces en domain, implementación en infrastructure)
- ✅ **Domain Exceptions** (excepciones de reglas de negocio)

### Arquitectura Hexagonal (Ports & Adapters)
```
domain/          ← Lógica de negocio pura (sin dependencias externas)
application/     ← Casos de uso y servicios de aplicación
infrastructure/  ← Implementación técnica (JPA, REST adapters)
interfaces/      ← Adaptadores de entrada (REST Controllers)
```

---

## Dominio: Biblioteca

### Aggregates (Entidades Raíz)

**1. Book (Libro)**
- Gestiona el inventario de libros
- Propiedades: ISBN, título, autor, copias disponibles
- Métodos de negocio: `borrowCopy()`, `returnCopy()`, `isAvailable()`

**2. Member (Socio)**
- Gestiona información de socios de la biblioteca
- Propiedades: MemberId, nombre, email, fecha de registro
- Métodos de negocio: `canBorrowBook()`, `hasOverdueLoans()`

**3. Loan (Préstamo)**
- Gestiona el préstamo de libros
- Propiedades: LoanId, memberId, ISBN, fecha préstamo, fecha devolución esperada
- Métodos de negocio: `returnBook()`, `isOverdue()`, `extend()`

### Value Objects

```java
ISBN           - Código único del libro (validación formato)
MemberId       - ID único del socio (TSID)
LoanId         - ID único del préstamo (TSID)
BookTitle      - Título del libro (no vacío, max 200 chars)
Email          - Email del socio (validación regex)
LoanPeriod     - Período del préstamo (start date, end date)
Author         - Autor del libro
CopiesCount    - Cantidad de copias (> 0)
```

### Domain Events

```java
BookAdded         - Un libro fue agregado al catálogo
BookBorrowed      - Una copia fue prestada
BookReturned      - Una copia fue devuelta
LoanOverdue       - Un préstamo está vencido
MemberRegistered  - Un nuevo socio se registró
```

### Reglas de Negocio

1. **Límite de préstamos**: Un socio puede tener máximo **3 libros** prestados simultáneamente
2. **Período de préstamo**: Cada préstamo dura **15 días** desde la fecha de inicio
3. **Disponibilidad**: No se puede prestar un libro sin copias disponibles
4. **Bloqueo por mora**: No se puede prestar a un socio con préstamos vencidos
5. **Validación de devolución**: Solo se puede devolver un libro efectivamente prestado

### Repository Interfaces (Ports)

```java
BookRepository
- void save(Book book)
- Optional<Book> findByISBN(ISBN isbn)
- List<Book> findAvailableBooks()

MemberRepository
- void save(Member member)
- Optional<Member> findById(MemberId id)
- Optional<Member> findByEmail(Email email)

LoanRepository
- void save(Loan loan)
- Optional<Loan> findById(LoanId id)
- List<Loan> findActiveByMemberId(MemberId memberId)
- List<Loan> findOverdueLoans()
```

---

## Servicios de Aplicación (Application Layer)

### BookService
- `addBook()` - Agregar libro al catálogo
- `findByISBN()` - Buscar libro por ISBN
- `findAvailableBooks()` - Listar libros disponibles
- `updateBook()` - Actualizar información del libro

### MemberService
- `registerMember()` - Registrar nuevo socio
- `findById()` - Buscar socio por ID
- `findByEmail()` - Buscar socio por email
- `getActiveLoansByMember()` - Obtener préstamos activos de un socio

### LoanService
- `borrowBook()` - Crear préstamo de libro
- `returnBook()` - Devolver libro prestado
- `extendLoan()` - Extender período de préstamo
- `findOverdueLoans()` - Listar préstamos vencidos
- `getLoanHistory()` - Obtener historial de préstamos

---

## Estructura de Paquetes

```
com.library/
├── shared/                      # Infraestructura compartida
│   ├── AggregateRoot.java
│   ├── DomainEvent.java
│   ├── DomainEventPublisher.java
│   └── exception/
│
├── books/                       # Módulo: Gestión de Libros
│   ├── domain/
│   │   ├── model/
│   │   │   └── Book.java        # Aggregate Root
│   │   ├── vo/
│   │   │   ├── ISBN.java
│   │   │   ├── BookTitle.java
│   │   │   ├── Author.java
│   │   │   └── CopiesCount.java
│   │   ├── repository/
│   │   │   └── BookRepository.java
│   │   ├── event/
│   │   │   ├── BookAdded.java
│   │   │   └── BookBorrowed.java
│   │   └── exception/
│   ├── application/
│   │   ├── BookService.java
│   │   └── dto/
│   │       ├── AddBookRequest.java
│   │       └── BookResponse.java
│   ├── infrastructure/
│   │   └── persistence/
│   │       ├── BookEntity.java
│   │       ├── BookRepositoryAdapter.java
│   │       └── JpaBookRepository.java
│   └── interfaces/
│       └── rest/
│           └── BookController.java
│
├── members/                     # Módulo: Gestión de Socios
│   ├── domain/
│   │   ├── model/Member.java
│   │   ├── vo/
│   │   │   ├── MemberId.java
│   │   │   └── Email.java
│   │   ├── repository/MemberRepository.java
│   │   └── event/MemberRegistered.java
│   ├── application/
│   │   ├── MemberService.java
│   │   └── dto/
│   ├── infrastructure/
│   │   └── persistence/
│   └── interfaces/
│       └── rest/
│
└── loans/                       # Módulo: Gestión de Préstamos
    ├── domain/
    │   ├── model/Loan.java
    │   ├── vo/
    │   │   ├── LoanId.java
    │   │   └── LoanPeriod.java
    │   ├── repository/LoanRepository.java
    │   └── event/
    │       ├── BookBorrowed.java
    │       └── BookReturned.java
    ├── application/
    │   ├── LoanService.java
    │   └── dto/
    ├── infrastructure/
    │   └── persistence/
    └── interfaces/
        └── rest/
```

---

## Ejemplo de Flujo: Prestar un Libro

```
1. Cliente → POST /api/loans (REST)
             { memberId: "123", isbn: "978-0134685991" }

2. LoanController → LoanService.borrowBook(memberId, isbn)

3. LoanService:
   a. Buscar Member por MemberId (usando MemberRepository)
   b. Validar: member.canBorrowBook() (max 3, sin mora)
   c. Buscar Book por ISBN (usando BookRepository)
   d. Validar: book.isAvailable() (copias > 0)
   e. Crear Loan.create(memberId, isbn, 15 días)
   f. book.borrowCopy() (decrementa copias disponibles)
   g. loanRepository.save(loan)
   h. bookRepository.save(book)
   i. Publicar eventos de dominio: BookBorrowed

4. EventListener → Recibe BookBorrowed (async)
                → Enviar email de confirmación

5. Respuesta → { loanId: "456", dueDate: "2026-01-25" }
```

---

## Tecnologías

- **Java 17+**
- **Spring Boot 3.x**
- **Spring Data JPA** (persistencia)
- **H2/PostgreSQL** (base de datos)
- **Maven** (build)
- **JUnit 5** (testing)
- **ArchUnit** (validación arquitectura)

---

## Referencias

Este proyecto está inspirado en:
- Repositorio: `spring-boot-application-architecture-patterns`
- Módulo de referencia: `meetup4j-modulith-ddd-ha`
- Guía de arquitectura: `ARCHITECTURE_GUIDE.md`

## Conceptos Clave a Aplicar

1. **Value Objects**: Validación en constructor, inmutabilidad con records
2. **Aggregate Root**: Encapsular reglas de negocio, registrar eventos de dominio
3. **Repository Pattern**: Interface en domain, implementación adapter en infrastructure
4. **Domain Events**: Comunicación desacoplada entre módulos (asíncrona)
5. **Application Services**: Orquestan casos de uso, delegan lógica al dominio
6. **Hexagonal Architecture**: Domain independiente de frameworks (JPA, Spring)

---

## Notas Importantes

- El **Domain** NO debe depender de JPA, Spring, o frameworks externos
- Usar **records** de Java para Value Objects (inmutabilidad automática)
- Validar en el constructor de Value Objects (fail-fast)
- Las **reglas de negocio** van en los **Aggregates**, NO en los Services
- Los **Services** solo orquestan, delegan la lógica al dominio
- Separar **Entity JPA** (infrastructure) de **Domain Model** (domain)
- Un **Aggregate** = Una **transacción** (consistencia transaccional)

---

**Fecha creación**: 2026-01-10
**Propósito**: Aprendizaje de DDD con ejemplo práctico
