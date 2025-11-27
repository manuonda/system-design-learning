package domain

import enums.StatusParkingSpace
import enums.TypeVehicle

/**
 * ParkingManager - Central manager for all parking spaces in the lot
 *
 * This class is responsible for managing the lifecycle of parking spaces,
 * including finding available spaces, assigning spaces to vehicles, and
 * releasing spaces when vehicles depart. It maintains an inventory of all
 * parking spaces and their current status.
 *
 * Design Pattern: Manager Pattern
 * Responsibilities:
 *   - Store and manage all parking spaces
 *   - Find available spaces for specific vehicle types
 *   - Track which spaces are occupied, available, or reserved
 *   - Release spaces when vehicles exit
 */
class ParkingManager {

    // Stores all parking spaces in the lot
    private val parkingSpaces: MutableList<ParkingSpace> = mutableListOf()

    /**
     * Adds a parking space to the lot
     *
     * @param space The ParkingSpace to add to the lot
     */
    fun addParkingSpace(space: ParkingSpace) {
        parkingSpaces.add(space)
    }

    /**
     * Finds the first available parking space suitable for the given vehicle type
     *
     * Searches through all parking spaces and returns the first one that is:
     * - Status: AVAILABLE
     * - Supports the requested vehicle type
     *
     * @param typeVehicle The type of vehicle looking for a space
     * @return A suitable ParkingSpace if found, null otherwise
     */
    fun findAvailabeSpaces(typeVehicle: TypeVehicle): ParkingSpace? {
        return parkingSpaces.firstOrNull { space ->
            space.status == StatusParkingSpace.AVAILABLE &&
                    space.availableTypeVehicles.contains(typeVehicle)
        }
    }

    /**
     * Retrieves all available spaces for a specific vehicle type
     *
     * @param typeVehicle The type of vehicle
     * @return List of available parking spaces for this vehicle type
     */
    fun getAvailableSpaces(typeVehicle: TypeVehicle): List<ParkingSpace> {
        return parkingSpaces.filter { space ->
            space.status == StatusParkingSpace.AVAILABLE &&
                    space.availableTypeVehicles.contains(typeVehicle)
        }
    }

    /**
     * Counts available spaces for a specific vehicle type
     *
     * @param typeVehicle The type of vehicle
     * @return Number of available spaces for this vehicle type
     */
    fun getAvailableSpacesCount(typeVehicle: TypeVehicle): Int {
        return getAvailableSpaces(typeVehicle).size
    }

    /**
     * Checks if at least one space is available for a vehicle type
     *
     * @param typeVehicle The type of vehicle
     * @return true if at least one suitable space is available, false otherwise
     */
    fun hasAvailableSpace(typeVehicle: TypeVehicle): Boolean {
        return getAvailableSpacesCount(typeVehicle) > 0
    }

    /**
     * Releases a parking space, marking it as AVAILABLE
     *
     * Called when a vehicle exits the parking lot. Sets the specified space
     * status back to AVAILABLE so it can be assigned to another vehicle.
     *
     * @param space The ParkingSpace to release
     */
    fun releaseSpace(space: ParkingSpace) {
        space.status = StatusParkingSpace.AVAILABLE
    }

    /**
     * Gets statistics about the parking lot occupancy
     *
     * @return Total number of parking spaces in the lot
     */
    fun getTotalSpaces(): Int = parkingSpaces.size

    /**
     * Gets the number of currently occupied spaces
     *
     * @return Count of spaces with OCCUPIED status
     */
    fun getOccupiedSpaces(): Int {
        return parkingSpaces.count { it.status == StatusParkingSpace.OCCUPIED }
    }
}