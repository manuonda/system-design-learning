package domain

import ReservationStatus
import enums.StatusParkingSpace
import enums.TypeVehicle
import java.time.LocalDateTime
import java.util.UUID

/**
 * Reservation Space Storage
 * @author: dgarcia
 * @since : 18/11/2025
 */
class StorageSpaceReservation(
    var reservationId: String = UUID.randomUUID().toString(),
    var confirmationCode: String = "",
    var status: ReservationStatus = ReservationStatus.EARRING,
    var startTime: LocalDateTime? = null,
    var endTime: LocalDateTime? = null,
    var assignedParkingSpace: ParkingSpace,
    val createdAt: LocalDateTime = LocalDateTime.now(),
) {


    fun createReservation(
        startTime: LocalDateTime,
        endTime: LocalDateTime,
        availableSpaces: List<ParkingSpace>,
        vehicle: Vehicle,
    ): String {

        if (!validateReservationData(startTime, endTime)) {
            return "Not Valid data for reservation"
        }
        if (!existAvilableParkinSpace(availableSpaces, vehicle)) {
            return "No available parking spaces for this vehicle type"
        }

        val filteredSpaces: List<ParkingSpace> = findAvailableSpaces(availableSpaces, vehicle)
        if (filteredSpaces.isEmpty()) {
            return "No available parking spaces found"
        }
        val assignedSpace = filteredSpaces.first()
        this.assignedParkingSpace = assignedSpace
        this.startTime = startTime
        this.endTime = endTime
        this.status = ReservationStatus.CONFIRMED


        return "Reservation created successfully"
    }


    private fun validateReservationData(
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ): Boolean {
        if (startTime.isAfter(endTime) || startTime.isBefore(LocalDateTime.now())) {
            return false
        }
        return true
    }

    /*
       Function to check if there is any available parking space
    */
    private fun existAvilableParkinSpace(listado: List<ParkingSpace>, vehicle: Vehicle): Boolean {
        return listado.any { parkingSpace ->
            val typeVehicleAvailable =
                parkingSpace.availableTypeVehicles.find { typeVehicle -> typeVehicle.equals(vehicle.typeVehicle) }
            parkingSpace.status == StatusParkingSpace.AVAILABLE && typeVehicleAvailable != null
        }
    }

    private fun findAvailableSpaces(spaces: List<ParkingSpace>, vehicle: Vehicle): List<ParkingSpace> {
        return spaces.filter { parkingSpace ->
            parkingSpace.status == StatusParkingSpace.AVAILABLE &&
                    parkingSpace.availableTypeVehicles.contains(vehicle.typeVehicle)
        }
    }
}
