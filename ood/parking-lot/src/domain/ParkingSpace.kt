package domain

import enums.SpaceType
import enums.StatusParkingSpace
import enums.TypeVehicle

/**
 * ParkingSpace - Represents an individual parking space in the parking lot
 *
 * A parking space is a physical location where a vehicle can be parked.
 * Each space has specific properties that determine what types of vehicles
 * it can accommodate and its current availability status.
 *
 * @property identifier Unique identifier for this parking space (e.g., "A1", "B5")
 * @property parkingLevel Level or floor number of this space in the parking structure
 * @property spaceType Type/category of the space (NORMAL, HANDICAPPED, COMPACT, OVERSIZED)
 * @property status Current status of the space (AVAILABLE, OCCUPIED, RESERVED, MAINTENANCE)
 * @property availableTypeVehicles List of vehicle types that are permitted to use this space
 */
data class ParkingSpace(
    val identifier: String,
    val parkingLevel: Int,
    val spaceType: SpaceType,
    var status: StatusParkingSpace,
    val availableTypeVehicles: List<TypeVehicle>
)

