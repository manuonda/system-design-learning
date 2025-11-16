# Parking Lot System - OOD

## Concept

A **Parking Lot System** is an application that manages a parking facility allowing users to:
- Enter/exit the parking lot
- Find available spaces
- Register entry and exit tickets
- Calculate fees based on parking duration
- Process payments
- Generate reports

The goal is to optimize the use of available space and maximize parking facility efficiency.

---

## Functional Requirements

### 1. **Parking Space Management**
- Create and register parking spaces
- Classify spaces by type:
  - Compact (for small vehicles)
  - Regular (for standard vehicles)
  - Large (for large vehicles)
  - Handicap (for disabled persons)
  - EV (for electric vehicles)
- Each space has a unique ID
- Each space has a status: Available, Occupied, Out of Service
- Display real-time availability

### 2. **Vehicle Management**
- Register vehicles with:
  - License plate
  - Vehicle type
  - Color
  - Brand/Model
- Validate that vehicle type is compatible with assigned space
- Allow the same vehicle to park multiple times

### 3. **Vehicle Entry**
- Search for available spaces of the correct type
- Assign a space to the vehicle
- Generate entry ticket with:
  - Space number
  - Entry time
  - Vehicle license plate
- Update space status to "Occupied"

### 4. **Vehicle Exit**
- Accept space number or vehicle license plate
- Validate the ticket
- Record exit time
- Calculate parking cost
- Mark space as "Available"

### 5. **Rate Calculation**
- Implement flexible pricing policy
- Options:
  - Hourly rate
  - Daily rate
  - Monthly rate
  - Discounts for extended parking
- Automatically calculate cost based on parking duration

### 6. **Payment Processing**
- Accept multiple payment methods:
  - Cash
  - Credit/Debit card
  - Digital wallet
- Record payment receipts
- Generate receipts

### 7. **Queries and Reports**
- Query available spaces by type
- Query parking history
- Revenue reports
- Occupancy reports

---

## Non-Functional Requirements

### 1. **Performance**
- Available space search < 100ms
- Entry/exit processing < 500ms
- System must support multiple simultaneous users

**Working tips:**
- Use HashMap or ConcurrentHashMap for O(1) lookups
- Maintain separate indexes by space type
- Implement available spaces cache

### 2. **Availability**
- High system availability (99.9%)
- Graceful error handling
- Automatic recovery from failures

**Working tips:**
- Implement comprehensive logging of all operations
- Use custom exceptions for different error types
- Design for graceful fallback (if no space, clearly reject entry)

### 3. **Scalability**
- Support multiple floors and sections
- Grow without architecture redesign
- Support 100% user growth

**Working tips:**
- Use Enums for space and vehicle types (type-safe)
- Design with interfaces to abstract behaviors
- Implement Strategy Pattern for flexible pricing

### 4. **Security**
- Protect vehicle and user data
- Audit financial transactions
- Validate user authentication

**Working tips:**
- Validate input early (valid license plate, supported type)
- Log all financial transactions
- Implement DAO (Data Access Objects) to abstract persistence

### 5. **Maintainability**
- Clean and well-structured code (OOD)
- Easy to extend
- Clear documentation
- Recognizable design patterns

**Working tips:**
- Separate business logic from persistence
- Create clear interfaces between components
- Use consistent naming (Vehicle vs Vehículo - choose one)
- Document important design decisions

### 6. **Usability**
- Intuitive interface
- Clear error messages
- Acceptable response times

**Working tips:**
- Define clear exceptions with descriptive messages
- Handle error cases explicitly
- Provide clear user feedback (entry successful, payment completed, etc.)