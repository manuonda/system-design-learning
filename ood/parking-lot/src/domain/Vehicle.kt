package domain

import enums.TypeVehicle

/*
  Data class vehicle
 */
data class Vehicle(
    val licensePlate: String,
    val owner: String,
    var typeVehicle: TypeVehicle
){

}