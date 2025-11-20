import domain.Vehicle
import enums.TypeVehicle
import java.util.UUID


fun main() {
    val vehicles: List<Vehicle> = listOf(
        Vehicle(UUID.randomUUID().toString(), "Vehicle Toyota Car", TypeVehicle.CAR),
        Vehicle(UUID.randomUUID().toString(), "", TypeVehicle.CAR),
    )
    for (vehicle in vehicles){
        System.out.println(vehicle)
    }
}