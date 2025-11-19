package domain

import domain.TypeVehicle

/**
/**
 * Represents a single parking space within a parking level.
 * 
 * A ParkingSpace is an individual slot for ONE vehicle at a time, not a container for multiple vehicles.
 * It manages its availability status and validates vehicle compatibility based on space type.
 * 
 * @property identifier Unique identifier for this parking space
 * @property typeVehicle The type of vehicle this space can accommodate
 * @property parkingLevel The level where this space is located
 * @property status Current occupancy status (AVAILABLE/OCCUPIED)
 * @property typeOfSpace The size/type of this parking space
 */
data class ParkingSpace(
    val identifier:String,
    var typeVehicle: TypeVehicle,
    var parkingLevel: Int,
    var status: StatusParkingSpace,
)
