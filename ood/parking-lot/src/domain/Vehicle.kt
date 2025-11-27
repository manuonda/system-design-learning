package domain

import enums.TypeVehicle

/**
 * Vehicle - Represents a vehicle in the parking lot system
 *
 * @property licensePlate Unique identifier of the vehicle (e.g., "ABC-123")
 * @property owner Name or identifier of the vehicle owner
 * @property typeVehicle Type of vehicle (CAR, MOTORCYCLE, TRUCK, BUS)
 */
data class Vehicle(
    val licensePlate: String,
    val owner: String,
    var typeVehicle: TypeVehicle
) {
    override fun toString(): String {
        return "Vehicle(licensePlate='$licensePlate', owner='$owner', type=$typeVehicle)"
    }
}