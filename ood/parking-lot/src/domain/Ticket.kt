package domain

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

/**
 * Ticket - Represents a parking session for a vehicle
 *
 * A ticket is generated when a vehicle enters the parking lot and contains
 * all information related to that parking session, including the vehicle,
 * assigned space, and timing information.
 *
 * @property ticketId Unique identifier for this parking ticket
 * @property vehicle The vehicle associated with this parking session
 * @property parkingSpace The parking space assigned to this vehicle
 * @property entryTime Timestamp when the vehicle entered the parking lot
 * @property exitTime Timestamp when the vehicle exits (null if still parked)
 */
data class Ticket(
    val ticketId: String,
    val vehicle: Vehicle,
    val parkingSpace: ParkingSpace,
    val entryTime: LocalDateTime,
    var exitTime: LocalDateTime? = null
) {
    /**
     * Determines if the vehicle is currently active/parked
     *
     * @return true if exitTime is null (vehicle still parked), false otherwise
     */
    fun isActive(): Boolean = exitTime == null

    /**
     * Calculates the duration of the parking session in hours
     *
     * @return Duration in hours if vehicle has exited, null if still parked
     */
    fun getDuration(): Long? {
        return if (exitTime != null) {
            ChronoUnit.HOURS.between(entryTime, exitTime)
        } else {
            null
        }
    }

    override fun toString(): String {
        val duration = getDuration()?.let { "$it hours" } ?: "Still parked"
        return "Ticket(id='$ticketId', vehicle='${vehicle.owner}' (${vehicle.licensePlate}), " +
                "space='${parkingSpace.identifier}', duration=$duration, status=${if (isActive()) "ACTIVE" else "COMPLETED"})"
    }
}
