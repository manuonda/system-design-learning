import domain.FareCalculator
import domain.ParkingLotFacade
import domain.ParkingManager
import domain.ParkingSpace
import domain.Vehicle
import enums.SpaceType
import enums.StatusParkingSpace
import enums.TypeVehicle
import java.lang.reflect.Type
import java.util.UUID


fun main() {

    val parkingManager = ParkingManager()
    val fareCalculator = FareCalculator()
    val parkingLotFacade = ParkingLotFacade(parkingManager , fareCalculator)

    val vehicles: List<Vehicle> = listOf(
        Vehicle(UUID.randomUUID().toString(), "Vehicle Toyota Car", TypeVehicle.CAR),
        Vehicle(UUID.randomUUID().toString(), "David Garcia", TypeVehicle.CAR),
        Vehicle(UUID.randomUUID().toString(), "David Silver", TypeVehicle.MOTORCYCLE),
        Vehicle(UUID.randomUUID().toString(),"Andres Garcia", TypeVehicle.BUS),
    )

    val spaces: List<ParkingSpace> = listOf(
        ParkingSpace(UUID.randomUUID().toString(),1, SpaceType.NORMAL,
            StatusParkingSpace.AVAILABLE,
            mutableListOf(TypeVehicle.CAR, TypeVehicle.BUS)),
        ParkingSpace(UUID.randomUUID().toString(),1, SpaceType.NORMAL,
            StatusParkingSpace.AVAILABLE,
            mutableListOf(TypeVehicle.CAR, TypeVehicle.MOTORCYCLE,TypeVehicle.BUS)),
        ParkingSpace(UUID.randomUUID().toString(),1, SpaceType.NORMAL,
            StatusParkingSpace.AVAILABLE,
            mutableListOf(TypeVehicle.CAR, TypeVehicle.BUS)),
       )

    for (space in spaces) {
        parkingManager.addParkingSpace(space)
    }

    for (vehicle in vehicles) {
        val ticket = parkingLotFacade.entry(vehicle)
        if (ticket != null) {
            System.out.println("Ticket Parking Vehiculo "+ vehicle.typeVehicle+ " - generated ticket : " + ticket.ticketId)
        } else {
            System.out.println("Not Found Workin Space  " + vehicle.typeVehicle + " license : " + vehicle.licensePlate)
        }

    }
    parkingLotFacade.exit(vehicles.get(0));
    System.out.println(" Total Spaces  : " + parkingManager.getTotalSpaces())
    System.out.println("Total Spaces Occupied " + parkingManager.getOccupiedSpaces().toString());
    System.out.println("Total Vehicles  : " + vehicles.size);

}