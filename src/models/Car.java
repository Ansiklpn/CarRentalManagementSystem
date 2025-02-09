package models;

public class Car {
    private int carId;
    private String make;
    private String model;
    private int year;
    private double rentalRate;
    private boolean isAvailable;

    public Car(int carId, String make, String model, int year, double rentalRate, boolean isAvailable) {
        this.carId = carId;
        this.make = make;
        this.model = model;
        this.year = year;
        this.rentalRate = rentalRate;
        this.isAvailable = isAvailable;
    }

    // Обновление статуса доступности автомобиля
    public void updateAvailability(boolean available) {
        this.isAvailable = available;
    }

    // Геттеры
    public int getCarId() { return carId; }
    public String getMake() { return make; }
    public String getModel() { return model; }
    public int getYear() { return year; }
    public double getRentalRate() { return rentalRate; }
    public boolean isAvailable() { return isAvailable; }
}
