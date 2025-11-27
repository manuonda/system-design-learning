# Parking Lot Management System - OOP Design

## Overview

A simple parking lot management system that enables vehicle entry, space allocation, exit processing, and fare calculation. The system uses OOP design patterns to maintain clean, scalable, and maintainable architecture.

**Version:** Simplified (no advance reservations, no complex payments)
**Focus:** Educational - Object-Oriented Design (OOD) Practice

---

## Architecture and Design Patterns

### Implemented Patterns

1. **Facade Pattern** (`ParkingLotFacade`)
   - Provides a simplified interface for users
   - Orchestrates interaction between ParkingManager, FareCalculator, and Ticket

2. **Manager Pattern** (`ParkingManager`)
   - Manages the lifecycle of parking spaces
   - Maintains inventory of available, occupied, and reserved spaces

3. **Strategy Pattern** (`FareCalculator`)
   - Encapsulates fare calculation logic
   - Allows future extension with different pricing strategies

4. **Data Class** (Kotlin)
   - `Vehicle`, `ParkingSpace`, `Ticket` as simple data models

---

## Functional Requirements (Simplified)

### FR-01: Vehicle Type Management

**Description:** The system supports multiple vehicle types.

**Supported Types:**
- `CAR` - Automobile (rate: $5/hour)
- `MOTORCYCLE` - Motorcycle (rate: $2/hour)
- `TRUCK` - Truck (rate: $10/hour)
- `BUS` - Bus (rate: $1/hour)

**Implementation:**
```kotlin
enum class TypeVehicle {
    CAR, MOTORCYCLE, TRUCK, BUS
}
```

---

### FR-02: Parking Space Management

**Description:** The system manages physical parking spaces.

**Space Properties:**
- `identifier` - Unique ID (e.g., "A1", "B5")
- `parkingLevel` - Floor/level number of the parking lot
- `spaceType` - Type of space (NORMAL, HANDICAPPED, COMPACT, OVERSIZED)
- `status` - Current status:
  - `AVAILABLE` - Available for parking
  - `OCCUPIED` - Currently occupied by a vehicle
  - `RESERVED` - Reserved (future)
  - `MAINTENANCE` - Under maintenance
- `availableTypeVehicles` - List of allowed vehicle types

**Acceptance Criteria:**
- Space can be created
- Each space has a well-defined status
- Real-time availability can be checked
- Spaces are automatically assigned by vehicle type

---

### FR-03: Vehicle Entry

**Description:** A vehicle enters the parking lot and a space is assigned.

**Flow:**
```
User arrives with vehicle
    |
    v
System searches for available space for that type
    |
    v
Space available?
  |-- YES -> Assign space + Create ticket -> Return ticket
  |-- NO -> Return null (no spaces available)
```

**Acceptance Criteria:**
- Space availability is validated
- Space compatible with vehicle type is assigned
- Unique ticket ID is generated
- Entry time is recorded
- Space status changes to OCCUPIED

**Implementation:**
```kotlin
fun entry(vehicle: Vehicle): Ticket?
```

---

### FR-04: Vehicle Exit

**Description:** A vehicle exits the parking lot and fare is calculated.

**Flow:**
```
User exits the parking lot
    |
    v
System retrieves their ticket
    |
    v
Calculate duration: exitTime - entryTime
    |
    v
Calculate fare: FareCalculator.calculateFare()
    |
    v
Release space (status = AVAILABLE)
    |
    v
Return fare to charge
```

**Acceptance Criteria:**
- Vehicle ticket is retrieved
- Exact exit time is recorded
- Parking duration is calculated
- Fare is calculated correctly
- Space is released

**Implementation:**
```kotlin
fun exit(vehicle: Vehicle): Double?  // Returns fare to charge
```

---

### FR-05: Fare Calculation

**Description:** Pricing system based on vehicle type and duration.

**Pricing Model:**
```
Total Fare = Base Rate x Duration (in hours)
```

**Base Rates:**
| Vehicle Type | Hourly Rate |
|-------------|------------|
| Automobile (CAR) | $5.00 |
| Motorcycle (MOTORCYCLE) | $2.00 |
| Truck (TRUCK) | $10.00 |
| Bus (BUS) | $1.00 |

**Calculation Example:**
```
Automobile parked for 3 hours:
  Fare = $5.00 x 3 = $15.00

Motorcycle parked for 2.5 hours:
  Fare = $2.00 x 2 = $4.00 (rounded to hours)
```

**Acceptance Criteria:**
- Fare is calculated correctly
- Vehicle type is considered
- Parking duration is considered
- Exception thrown for unknown vehicle type

---

## Main Classes

### 1. Vehicle
Represents a vehicle in the system.

```kotlin
data class Vehicle(
    val licensePlate: String,      // Vehicle license plate
    val owner: String,              // Vehicle owner name
    var typeVehicle: TypeVehicle   // Vehicle type
)
```

---

### 2. ParkingSpace
Represents an individual parking space.

```kotlin
data class ParkingSpace(
    val identifier: String,                      // Unique ID (e.g., "A1")
    val parkingLevel: Int,                       // Floor/level
    val spaceType: SpaceType,                    // Type of space
    var status: StatusParkingSpace,              // Current status
    val availableTypeVehicles: List<TypeVehicle> // Allowed vehicle types
)
```

---

### 3. Ticket
Represents a parking session.

```kotlin
data class Ticket(
    val ticketId: String,           // Unique ticket ID
    val vehicle: Vehicle,            // Parked vehicle
    val parkingSpace: ParkingSpace,  // Assigned space
    val entryTime: LocalDateTime,    // Entry time
    var exitTime: LocalDateTime?     // Exit time (null if active)
)
```

**Methods:**
- `isActive(): Boolean` - Indicates if vehicle is still parked
- `getDuration(): Long?` - Calculates duration in hours

---

### 4. ParkingManager
Central manager for parking spaces.

```kotlin
class ParkingManager {
    fun addParkingSpace(space: ParkingSpace)
    fun findAvailabeSpaces(typeVehicle: TypeVehicle): ParkingSpace?
    fun getAvailableSpaces(typeVehicle: TypeVehicle): List<ParkingSpace>
    fun getAvailableSpacesCount(typeVehicle: TypeVehicle): Int
    fun hasAvailableSpace(typeVehicle: TypeVehicle): Boolean
    fun releaseSpace(space: ParkingSpace)
    fun getTotalSpaces(): Int
    fun getOccupiedSpaces(): Int
}
```

---

### 5. FareCalculator
Calculates parking fares.

```kotlin
class FareCalculator {
    fun calculateFare(vehicleType: TypeVehicle, durationHours: Long): Double
    fun getBaseFare(vehicleType: TypeVehicle): Double
    fun getAllFares(): Map<TypeVehicle, Double>
}
```

---

### 6. ParkingLotFacade (Main Interface)
Facade that simplifies all parking lot interactions.

```kotlin
class ParkingLotFacade(
    private val parkingManager: ParkingManager,
    private val fareCalculator: FareCalculator
) {
    fun entry(vehicle: Vehicle): Ticket?
    fun exit(vehicle: Vehicle): Double?
    fun getActiveTicket(licensePlate: String): Ticket?
    fun getActiveSessionsCount(): Int
}
```

---

## Complete Use Case

### Scenario: Customer parks a car for 2 hours

```
1. ENTRY
   Customer: "I want to park my car (ABC-123)"
   System:
   |- Searches for available space for CAR
   |- Finds space A1
   |- Creates ticket #12345
   |- Returns: "Ticket generated, space A1"

2. [Car parked for 2 hours]

3. EXIT
   Customer: "I'm leaving"
   System:
   |- Retrieves ticket #12345
   |- Records current exit time
   |- Calculates duration: 2 hours
   |- Calculates fare: $5/hour x 2h = $10
   |- Releases space A1
   |- Returns: "Fare: $10.00"
```

---

## UML Class Diagram

To view the complete class diagram, see the file: **[diagrama.puml](./diagrama.puml)**

The diagram includes:
- 3 Enums: TypeVehicle, SpaceType, StatusParkingSpace
- 6 Main Classes: Vehicle, ParkingSpace, Ticket, ParkingManager, FareCalculator, ParkingLotFacade
- Class Relationships: Dependencies, composition, associations
- Design Patterns Annotated: Facade, Manager, Strategy

**To visualize the PlantUML diagram:**
1. Use PlantUML extension in VS Code
2. Or copy the content to https://www.plantuml.com/plantuml/uml/
3. Or run `plantuml diagrama.puml` in terminal

---

## Simplified Data Flow

```
ParkingLotFacade (Facade)
  |
  +-- ParkingManager        FareCalculator
       |- Spaces             |- Base rates
       |- Availability       |- Calculations
       |
       +-- Ticket (Parking Session)
            |- ID
            |- Vehicle
            |- Assigned space
            |- Times (entry/exit)
```

---

## Project Structure

```
src/
  domain/
    |- Vehicle.kt              # Vehicle model
    |- ParkingSpace.kt         # Parking space model
    |- Ticket.kt               # Ticket model
    |- ParkingManager.kt       # Space manager
    |- FareCalculator.kt       # Fare calculator
    |- ParkingLotFacade.kt     # Main facade
  enums/
    |- TypeVehicle.kt          # Vehicle types
    |- SpaceType.kt            # Space types
    |- StatusParkingSpace.kt   # Space statuses
  Main.kt                       # Usage example
```

---

## How to Use

### Initialize System

```kotlin
fun main() {
    // Create components
    val parkingManager = ParkingManager()
    val fareCalculator = FareCalculator()
    val parkingLot = ParkingLotFacade(parkingManager, fareCalculator)

    // Add spaces
    val space1 = ParkingSpace(
        identifier = "A1",
        parkingLevel = 1,
        spaceType = SpaceType.NORMAL,
        status = StatusParkingSpace.AVAILABLE,
        availableTypeVehicles = listOf(TypeVehicle.CAR, TypeVehicle.BUS)
    )
    parkingManager.addParkingSpace(space1)

    // Create vehicle
    val car = Vehicle(
        licensePlate = "ABC-123",
        owner = "John Doe",
        typeVehicle = TypeVehicle.CAR
    )

    // ENTRY: Vehicle enters
    val ticket = parkingLot.entry(car)
    if (ticket != null) {
        println("Ticket generated: ${ticket.ticketId}")
        println("Assigned space: ${ticket.parkingSpace.identifier}")
    }

    // [Vehicle is parked...]

    // EXIT: Vehicle leaves
    val fare = parkingLot.exit(car)
    if (fare != null) {
        println("Fare to pay: $$fare")
    }
}
```

---

## Non-Functional Requirements (Simplified)

| Requirement | Fulfillment |
|------------|-------------|
| Performance | O(1) for space search using firstOrNull() |
| Scalability | Handles thousands of spaces without issues |
| Maintainability | Clean code, well documented, OOD patterns |
| Extensibility | Easy to add new vehicle types and pricing strategies |
| Simplicity | No reservations, no complex payments, only entry/exit |

---

## Differences: Simplified vs Complete Version

| Aspect | Simplified | Complete |
|--------|-----------|---------|
| Reservations | No | Yes (PENDING, CONFIRMED, etc.) |
| Payments | Simple fare | Refunds, discounts |
| Cancellations | No | Yes, with policies |
| No-shows | No | Yes, automatic |
| Audit | No | Yes, complete |
| Complexity | Low | High |
| Main Classes | 6 | 10+ |
| Lines of Code | ~400 | ~1000+ |
| Purpose | Learning OOD | Production system |

---

## Notes

- System designed for educational purposes
- Focus on SOLID principles and design patterns
- Easy to extend and maintain
- Solid foundation for adding more complex features

---

## OOD Concepts Learned

- Encapsulation - Private properties, public methods
- Abstraction - Simplified interfaces through Facade
- Inheritance - Inheritance with enums
- Polymorphism - Different vehicle types, different rates
- Composition - Objects contain other objects (Facade contains Manager, etc.)
- Design Patterns - Facade, Manager, Strategy, Data Class

---

## Possible Future Improvements

1. Advance reservation system
2. Real payment processing
3. Notifications and alerts
4. Reports and statistics
5. Database persistence
6. Authentication and authorization
7. REST API for mobile application

---

## Author

**David Garcia** (@manuonda)
