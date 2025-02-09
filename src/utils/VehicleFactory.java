package utils;

import models.Car;

public class VehicleFactory {
    public static Car createVehicle(int carId, String type, String make, String model, int year, double rentalRate) {
        // The "type" parameter can be used to create different subclasses of Car if needed.
        return new Car(carId, make, model, year, rentalRate, true);
    }
}
