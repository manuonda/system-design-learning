package enums

enum class TypeVehicle(
  val code:String,
    val description: String,
    val widthSize: Double,
    val heightSize: Double,
){
    CAR("CAR", "Automobile", 2.0, 1.5),
    MOTORCYCLE("MOTORCYCLE", "Motorcycle", 1.0, 0.8),
    TRUCK("TRUCK", "Truck", 3.5, 2.5),
    BUS("BUS", "Bus", 4.0, 3.0)
}