package domain

import java.time.LocalDate
import java.time.LocalTime
import java.time.LocalDateTime
import java.util.UUID

/**
 * Reservation Space Storage
 * @author: dgarcia
 * @since : 18/11/2025
 */
class StorageSpaceReservation(
    val typeVehicle: TypeVehicle
) {
  
   //Dates of parking reservation
   var fechaEntrada: LocalDate? = null
   var horaEntrada: LocalTime? = null
   var fechaSalida: LocalDate? = null
   var horaSalida: LocalTime?  = null

   //Dates of vehicle
   var licensePlate: String? = null

   //Dates of reservation
   var reservationId: String = UUID.randomUUID().toString()
   var confirmationCode:  String = ""
   var status: ReservationStatus = ReservationStatus.EARRING
   var assignedParkingSpace: ParkingSpace? = null
   var createdAt: LocalDateTime = LocalDateTime.now()

  fun constructor(){

  }

  fun createReservation(
      startTime: LocalDateTime,
      endTime: LocalDateTime,
      availableSpaces: List<ParkingSpace>,
      vehiclePlate: String? = null,
      vehicle: Vehicle,
  ):String{
   //Validar datos si son correctos para crear la reserva 

   if(!validateReservationData(startTime, endTime)){
       return "Not Valid data for reservation"
   }
   if (!existAvilableParkinSpace(availableSpaces, vehicle)) {
     return "No available parking spaces for this vehicle type"
   }

   val filteredSpaces: List<ParkingSpace> = findAvailableSpaces(availableSpaces, vehicle)
   if (filteredSpaces.isEmpty()){
      return "No available parking spaces found"
   }
   val assignedSpace = filteredSpaces.first()
   this.assignedParkingSpace = assignedSpace


   return "Reservation created successfully"
  }


  fun validateReservationData(
     startTime: LocalDateTime, 
     endTime: LocalDateTime
  ):Boolean {
    if(startTime.isAfter(endTime) || startTime.isBefore(LocalDateTime.now())){
       return false
    }
    return true
  }
  /*
     Function to check if there is any available parking space
  */
  fun existAvilableParkinSpace(listado: List<ParkingSpace>, vehicle: Vehicle): Boolean{
     return listado.any { parkingSpace -> 
       parkingSpace.status == StatusParkingSpace.AVAILABLE && parkingSpace.typeVehicle == vehicle.typeVehicle
     }
  }


  /**
   * Function to find all available parking spaces
   */
  fun findAvailableSpaces(listado: List<ParkingSpace>, vehicle: Vehicle): List<ParkingSpace>{
         return listado.filter { parkingSpace ->
            parkingSpace.status == StatusParkingSpace.AVAILABLE &&
            parkingSpace.typeVehicle == vehicle.typeVehicle
         }
  }

 

}
