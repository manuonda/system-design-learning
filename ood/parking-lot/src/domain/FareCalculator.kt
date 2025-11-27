package domain

import enums.TypeVehicle

/**
 * FareCalculator - Calculates parking fees based on vehicle type and duration
 *
 * This class implements the pricing strategy for the parking lot. It determines
 * the cost for a parking session based on:
 * - The type of vehicle (AUTO, MOTO, CAMION)
 * - The duration of the parking session in hours
 *
 * Design Pattern: Strategy Pattern (can be extended with different pricing strategies)
 * Pricing Model: Hourly rate multiplied by duration
 */
class FareCalculator {

    // Base hourly rates for each vehicle type
    // These rates define the cost per hour for parking
    private val baseFares: Map<TypeVehicle, Double> = mapOf(
        TypeVehicle.CAR to 5.0,         // Antes: AUTO
        TypeVehicle.MOTORCYCLE to 2.0,
        TypeVehicle.TRUCK to 10.0,
        TypeVehicle.BUS to 1.0
    )

    /**
     * Calculates the total fare for a parking session
     *
     * Formula: baseFare × durationHours
     *
     * @param vehicleType The type of vehicle that is parking
     * @param durationHours The number of hours the vehicle was parked
     * @return The total fare amount to charge
     * @throws IllegalArgumentException if the vehicle type is not supported
     */
    fun calculateFare(vehicleType: TypeVehicle, durationHours: Long): Double {
        val baseFare = baseFares[vehicleType]
            ?: throw IllegalArgumentException("Vehicle type not supported: $vehicleType")
        return baseFare * durationHours
    }

    /**
     * Retrieves the base hourly rate for a vehicle type
     *
     * @param vehicleType The type of vehicle
     * @return The base hourly rate, or 0.0 if vehicle type is unknown
     */
    fun getBaseFare(vehicleType: TypeVehicle): Double {
        return baseFares[vehicleType] ?: 0.0
    }

    /**
     * Gets all configured vehicle types and their rates
     *
     * @return A map of all vehicle types and their corresponding hourly rates
     */
    fun getAllFares(): Map<TypeVehicle, Double> = baseFares.toMap()
}