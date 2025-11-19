package domain

data class Vehicle(
    val codigo: String,
    val description: String,
    var typeVehicle: TypeVehicle
)