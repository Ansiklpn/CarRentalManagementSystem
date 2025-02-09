// File: src/models/Booking.java
package models;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Booking {
    private int bookingId;
    private User user;
    private Car car;
    private LocalDate startDate;
    private LocalDate endDate;
    private double totalCost;

    public Booking(int bookingId, User user, Car car, LocalDate startDate, LocalDate endDate) {
        this.bookingId = bookingId;
        this.user = user;
        this.car = car;
        this.startDate = startDate;
        this.endDate = endDate;
        recalculateTotalCost();
    }

    // Пересчитывает общую стоимость бронирования на основе количества дней и тарифа аренды автомобиля
    private void recalculateTotalCost() {
        long days = ChronoUnit.DAYS.between(startDate, endDate);
        this.totalCost = days * car.getRentalRate();
    }

    // Альтернативный метод расчета стоимости бронирования
    public double calculateCost() {
        long days = ChronoUnit.DAYS.between(startDate, endDate);
        return days * car.getRentalRate();
    }

    // Подтверждение бронирования с выводом детальной информации
    public void confirmBooking() {
        System.out.println("Бронирование подтверждено для " + user.getName() +
                " на автомобиль " + car.getMake() + " " + car.getModel() + ".");
        System.out.println("Период бронирования: с " + startDate + " по " + endDate +
                ". Общая стоимость: " + totalCost);
    }

    // Метод для обновления дат бронирования
    public void updateBookingDates(LocalDate newStartDate, LocalDate newEndDate) {
        if (newStartDate != null && newEndDate != null && newEndDate.isAfter(newStartDate)) {
            this.startDate = newStartDate;
            this.endDate = newEndDate;
            recalculateTotalCost();
            System.out.println("Даты бронирования обновлены. Новый период: с " + startDate + " по " + endDate +
                    ". Новая стоимость: " + totalCost);
        } else {
            System.out.println("Неверные даты для обновления бронирования. Проверьте, что новая конечная дата позже начальной.");
        }
    }

    // Геттеры для доступа к полям
    public int getBookingId() {
        return bookingId;
    }

    public User getUser() {
        return user;
    }

    public Car getCar() {
        return car;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public double getTotalCost() {
        return totalCost;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "bookingId=" + bookingId +
                ", user=" + user.getName() +
                ", car=" + car.getMake() + " " + car.getModel() +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", totalCost=" + totalCost +
                '}';
    }
}
