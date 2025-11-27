package domain

import enums.StatusParkingSpace
import java.time.LocalDateTime
import java.util.UUID

/**
 * ParkingLotFacade - Facade that provides a simplified interface to the parking lot system
 *
 * This class implements the Facade design pattern and serves as the main entry point
 * for the parking lot operations. It orchestrates the interactions between:
 * - ParkingManager: manages available spaces
 * - FareCalculator: calculates parking fees
 * - Ticket: records parking sessions
 *
 * Users of the parking lot only need to interact with this class through:
 * - entry(): to park a vehicle
 * - exit(): to retrieve a vehicle and calculate the fee
 *
 * Design Pattern: Facade Pattern
 * Responsibilities:
 *   - Simplify the interface for parking lot operations
 *   - Coordinate vehicle entry into the lot
 *   - Coordinate vehicle exit and fare calculation
 *   - Maintain active parking sessions
 */
class ParkingLotFacade(
    private val parkingManager: ParkingManager,
    private val fareCalculator: FareCalculator
) {
    // Stores active parking sessions mapped by vehicle license plate
    private val activeTickets: MutableMap<String, Ticket> = mutableMapOf()

    /**
     * Processes vehicle entry into the parking lot
     *
     * This method:
     * 1. Checks if a suitable parking space is available
     * 2. Marks the space as OCCUPIED
     * 3. Creates and stores a new ticket for this parking session
     *
     * @param vehicle The vehicle requesting to park
     * @return A Ticket if a suitable space is found and assigned, null if no space available
     */
    fun entry(vehicle: Vehicle): Ticket? {
        // Find an available space for this vehicle type
        val spaceAvailable = parkingManager.findAvailabeSpaces(vehicle.typeVehicle) ?: return null

        // Mark the space as occupied
        spaceAvailable.status = StatusParkingSpace.OCCUPIED

        // Create a new ticket for this parking session
        val ticket = Ticket(
            ticketId = UUID.randomUUID().toString(),
            vehicle = vehicle,
            parkingSpace = spaceAvailable,
            entryTime = LocalDateTime.now()
        )

        // Store the ticket to track active parking sessions
        activeTickets[vehicle.licensePlate] = ticket

        return ticket
    }

    /**
     * Processes vehicle exit from the parking lot
     *
     * This method:
     * 1. Retrieves the ticket for the exiting vehicle
     * 2. Calculates the parking duration
     * 3. Computes the fare based on vehicle type and duration
     * 4. Releases the parking space back to available
     * 5. Removes the ticket from active sessions
     *
     * @param vehicle The vehicle that is exiting
     * @return The calculated fare amount to charge, or null if vehicle not found
     */
    fun exit(vehicle: Vehicle): Double? {
        // Retrieve the active ticket for this vehicle
        val ticket = activeTickets[vehicle.licensePlate] ?: return null

        // Record the exit time
        ticket.exitTime = LocalDateTime.now()

        // Calculate parking duration in hours
        val durationHours = ticket.getDuration() ?: 0L

        // Calculate the fare based on vehicle type and duration
        val fare = fareCalculator.calculateFare(
            vehicle.typeVehicle,
            durationHours
        )

        // Release the parking space for other vehicles
        parkingManager.releaseSpace(ticket.parkingSpace)
        ticket.parkingSpace.status = StatusParkingSpace.AVAILABLE

        // Remove the completed ticket from active sessions
        activeTickets.remove(vehicle.licensePlate)

        return fare
    }

    /**
     * Retrieves the active ticket for a vehicle
     *
     * @param licensePlate The vehicle's license plate
     * @return The active Ticket if found, null otherwise
     */
    fun getActiveTicket(licensePlate: String): Ticket? {
        return activeTickets[licensePlate]
    }

    /**
     * Gets the number of currently active parking sessions
     *
     * @return Count of vehicles currently parked
     */
    fun getActiveSessionsCount(): Int {
        return activeTickets.size
    }
}