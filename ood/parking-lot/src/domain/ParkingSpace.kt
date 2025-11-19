package domain

/**
 * ParkingSpace - Representa un espacio individual de estacionamiento
 *
 * @property identifier Identificador único del espacio
 * @property parkingLevel Nivel del estacionamiento
 * @property spaceType Tipo de espacio
 * @property status Estado actual del espacio
 * @property availableTypeVehicles Lista de tipos de vehículos permitidos
 */
data class ParkingSpace(
    val identifier: String,
    val parkingLevel: Int,
    val spaceType: SpaceType,
    var status: StatusParkingSpace,
    val availableTypeVehicles: List<TypeVehicle>
)

