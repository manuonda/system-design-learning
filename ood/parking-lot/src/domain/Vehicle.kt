package domain

data class Vehicle(
    val codigo: String,
    val description: String,
    val sizeWidth: Double,
    val sizeHeight: Double,
    val priceForHour: Double,
    var typeVehicle: TypeVehicle
)