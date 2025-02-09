import models.User;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Scanner;

// Main application for the Car Rental System
public class CarRentalSystem {
    // DatabaseHandler handles the connection to PostgreSQL.
    // Make sure your DatabaseHandler class is correctly implemented.
    private static database.DatabaseHandler databaseHandler = new database.DatabaseHandler();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n=== Car Rental System ===");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");

            String input = scanner.nextLine().trim();
            int choice;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid choice. Please enter a number (1-3).");
                continue;
            }

            switch (choice) {
                case 1:
                    login();
                    break;
                case 2:
                    register();
                    break;
                case 3:
                    System.out.println("Exiting the system. Goodbye!");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    // Method for user login
    private static void login() {
        System.out.print("Enter your user ID: ");
        String input = scanner.nextLine().trim();
        int userId;
        try {
            userId = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid user ID. Please enter a number.");
            return;
        }
        User user = getUserFromDatabase(userId);
        if (user == null) {
            System.out.println("User not found. Please register first.");
            return;
        }
        if ("Admin".equalsIgnoreCase(user.getRole())) {
            System.out.print("Enter admin password: ");
            String password = scanner.nextLine();
            // Hardcoded admin password for demonstration (change as needed)
            if ("admin123".equals(password)) {
                adminMenu();
            } else {
                System.out.println("Invalid password.");
            }
        } else {
            customerMenu(user);
        }
    }

    // Method for new user registration
    private static void register() {
        System.out.print("Enter user ID: ");
        String input = scanner.nextLine().trim();
        int userId;
        try {
            userId = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid user ID. Please enter a number.");
            return;
        }

        // Check if user ID already exists
        User existingUser = getUserFromDatabase(userId);
        if (existingUser != null) {
            System.out.println("User ID already exists. Registration failed.");
            return;
        }

        System.out.print("Enter user name: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("Name cannot be empty. Registration failed.");
            return;
        }

        System.out.print("Enter user email: ");
        String email = scanner.nextLine().trim();
        if (email.isEmpty()) {
            System.out.println("Email cannot be empty. Registration failed.");
            return;
        }

        // Check if email already exists in the database
        if (isEmailAlreadyExists(email)) {
            System.out.println("Email already exists. Registration failed.");
            return;
        }

        System.out.print("Enter user role (Admin/Customer): ");
        String role = scanner.nextLine().trim();

        // Validate role
        if (!("Admin".equalsIgnoreCase(role) || "Customer".equalsIgnoreCase(role))) {
            System.out.println("Invalid role. Please enter Admin or Customer.");
            return;
        }

        // For admin registration, check a registration password
        if ("Admin".equalsIgnoreCase(role)) {
            System.out.print("Enter admin registration password: ");
            String password = scanner.nextLine();
            if (!"admin123".equals(password)) {
                System.out.println("Invalid admin password. Registration failed.");
                return;
            }
        }

        // Insert new user into the database
        String query = "INSERT INTO users (user_id, name, email, role) VALUES (?, ?, ?, ?)";

        try (Connection connection = databaseHandler.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, userId);
            statement.setString(2, name);
            statement.setString(3, email);
            statement.setString(4, role);

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("User registered successfully!");
            } else {
                System.out.println("Failed to register user.");
            }
        } catch (SQLException e) {
            System.err.println("Error registering user: " + e.getMessage());
        }
    }

    // Check if the provided email already exists in the database
    private static boolean isEmailAlreadyExists(String email) {
        String query = "SELECT * FROM users WHERE email = ?";

        try (Connection connection = databaseHandler.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();

            return resultSet.next(); // Returns true if email exists
        } catch (SQLException e) {
            System.err.println("Error checking email: " + e.getMessage());
            return false;
        }
    }

    // Admin menu with car fleet management options
    private static void adminMenu() {
        while (true) {
            System.out.println("\n=== Admin Menu ===");
            System.out.println("1. View all cars");
            System.out.println("2. Add a new car");
            System.out.println("3. Delete a car");
            System.out.println("4. Update car details");
            System.out.println("5. View statistics");
            System.out.println("6. Back to main menu");
            System.out.print("Enter your choice: ");

            String input = scanner.nextLine().trim();
            int choice;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid choice. Please enter a number (1-6).");
                continue;
            }

            switch (choice) {
                case 1:
                    viewAllCars();
                    break;
                case 2:
                    addNewCar();
                    break;
                case 3:
                    deleteCar();
                    break;
                case 4:
                    updateCarDetails();
                    break;
                case 5:
                    viewStatistics();
                    break;
                case 6:
                    return; // Return to main menu
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    // Update details of a specific car in the fleet
    private static void updateCarDetails() {
        System.out.print("Enter the Car ID to update: ");
        String carIdInput = scanner.nextLine().trim();
        int carId;
        try {
            carId = Integer.parseInt(carIdInput);
        } catch (NumberFormatException e) {
            System.out.println("Invalid Car ID. Please enter a number.");
            return;
        }
        System.out.print("Enter new make (leave blank to keep unchanged): ");
        String make = scanner.nextLine().trim();
        System.out.print("Enter new model (leave blank to keep unchanged): ");
        String model = scanner.nextLine().trim();
        System.out.print("Enter new year (-1 to keep unchanged): ");
        int year = scanner.nextInt();
        System.out.print("Enter new rental rate (-1 to keep unchanged): ");
        double rate = scanner.nextDouble();
        scanner.nextLine(); // Consume newline

        String query = "UPDATE cars SET make = COALESCE(NULLIF(?, ''), make), " +
                "model = COALESCE(NULLIF(?, ''), model), " +
                "year = COALESCE(NULLIF(?, -1), year), " +
                "rental_rate = COALESCE(NULLIF(?, -1), rental_rate) " +
                "WHERE car_id = ?";

        try (Connection connection = databaseHandler.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, make);
            statement.setString(2, model);
            statement.setInt(3, year);
            statement.setDouble(4, rate);
            statement.setInt(5, carId);

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Car details updated successfully!");
            } else {
                System.out.println("No car found with the given ID.");
            }
        } catch (SQLException e) {
            System.err.println("Error updating car: " + e.getMessage());
        }
    }

    // View simple statistics about the car fleet and bookings
    private static void viewStatistics() {
        String totalCarsQuery = "SELECT COUNT(*) AS total_cars FROM cars";
        String bookedCarsQuery = "SELECT COUNT(*) AS booked_cars FROM cars WHERE is_available = false";
        String totalBookingsQuery = "SELECT COUNT(*) AS total_bookings FROM bookings";

        try (Connection connection = databaseHandler.getConnection()) {
            try (PreparedStatement totalCarsStmt = connection.prepareStatement(totalCarsQuery);
                 ResultSet carsResult = totalCarsStmt.executeQuery()) {
                if (carsResult.next()) {
                    System.out.println("Total Cars in Fleet: " + carsResult.getInt("total_cars"));
                }
            }

            try (PreparedStatement bookedCarsStmt = connection.prepareStatement(bookedCarsQuery);
                 ResultSet bookedResult = bookedCarsStmt.executeQuery()) {
                if (bookedResult.next()) {
                    System.out.println("Total Booked Cars: " + bookedResult.getInt("booked_cars"));
                }
            }

            try (PreparedStatement totalBookingsStmt = connection.prepareStatement(totalBookingsQuery);
                 ResultSet bookingsResult = totalBookingsStmt.executeQuery()) {
                if (bookingsResult.next()) {
                    System.out.println("Total Bookings: " + bookingsResult.getInt("total_bookings"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving statistics: " + e.getMessage());
        }
    }

    // Delete a car from the fleet based on its ID
    private static void deleteCar() {
        System.out.print("Enter the Car ID to delete: ");
        String carIdInput = scanner.nextLine().trim();
        int carId;
        try {
            carId = Integer.parseInt(carIdInput);
        } catch (NumberFormatException e) {
            System.out.println("Invalid Car ID. Please enter a number.");
            return;
        }

        String query = "DELETE FROM cars WHERE car_id = ?";

        try (Connection connection = databaseHandler.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, carId);
            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Car deleted successfully!");
            } else {
                System.out.println("No car found with the given ID.");
            }
        } catch (SQLException e) {
            System.err.println("Error deleting car: " + e.getMessage());
        }
    }

    // Add a new car to the fleet
    private static void addNewCar() {
        System.out.print("Enter car make: ");
        String make = scanner.nextLine();
        System.out.print("Enter car model: ");
        String model = scanner.nextLine();
        System.out.print("Enter car year: ");
        int year = scanner.nextInt();
        System.out.print("Enter rental rate: ");
        double rentalRate = scanner.nextDouble();
        scanner.nextLine(); // Consume newline

        String query = "INSERT INTO cars (make, model, year, rental_rate, is_available) VALUES (?, ?, ?, ?, true)";

        try (Connection connection = databaseHandler.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, make);
            statement.setString(2, model);
            statement.setInt(3, year);
            statement.setDouble(4, rentalRate);

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Car added successfully!");
            } else {
                System.out.println("Failed to add car.");
            }
        } catch (SQLException e) {
            System.err.println("Error adding car: " + e.getMessage());
        }
    }

    // Customer menu for viewing available cars and booking a car
    private static void customerMenu(User customer) {
        while (true) {
            System.out.println("\n=== Customer Menu ===");
            System.out.println("1. View available cars");
            System.out.println("2. Book a car");
            System.out.println("3. Back to main menu");
            System.out.print("Enter your choice: ");

            String input = scanner.nextLine().trim();
            int choice;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid choice. Please enter a number (1-3).");
                continue;
            }

            switch (choice) {
                case 1:
                    viewAllCars();
                    break;
                case 2:
                    bookCar(customer);
                    break;
                case 3:
                    return; // Exit back to main menu
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    // Book a car by selecting from available cars and specifying rental days, using a transaction
    private static void bookCar(User customer) {
        System.out.println("\nAvailable cars for booking:");
        String query = "SELECT * FROM cars WHERE is_available = true";
        try (Connection connection = databaseHandler.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                System.out.printf("Car ID: %d, Make: %s, Model: %s, Year: %d, Rental Rate: %.2f%n",
                        resultSet.getInt("car_id"),
                        resultSet.getString("make"),
                        resultSet.getString("model"),
                        resultSet.getInt("year"),
                        resultSet.getDouble("rental_rate"));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching available cars: " + e.getMessage());
            return;
        }

        System.out.print("Enter the Car ID you want to book: ");
        String carIdInput = scanner.nextLine().trim();
        int carId;
        try {
            carId = Integer.parseInt(carIdInput);
        } catch (NumberFormatException e) {
            System.out.println("Invalid Car ID. Please enter a number.");
            return;
        }

        System.out.print("Enter number of rental days: ");
        String daysInput = scanner.nextLine().trim();
        int rentalDays;
        try {
            rentalDays = Integer.parseInt(daysInput);
        } catch (NumberFormatException e) {
            System.out.println("Invalid number of days. Please enter a number.");
            return;
        }

        Connection connection = null;
        try {
            connection = databaseHandler.getConnection();
            connection.setAutoCommit(false); // Start transaction

            // Check if the car is still available
            String checkQuery = "SELECT * FROM cars WHERE car_id = ? AND is_available = true";
            try (PreparedStatement checkStatement = connection.prepareStatement(checkQuery)) {
                checkStatement.setInt(1, carId);
                ResultSet rs = checkStatement.executeQuery();
                if (rs.next()) {
                    double rentalRate = rs.getDouble("rental_rate");

                    LocalDate startDate = LocalDate.now();
                    LocalDate endDate = startDate.plusDays(rentalDays);
                    double totalCost = rentalDays * rentalRate;

                    // Insert a new booking record
                    String bookingQuery = "INSERT INTO bookings (user_id, car_id, start_date, end_date, total_cost) VALUES (?, ?, ?, ?, ?)";
                    try (PreparedStatement bookingStatement = connection.prepareStatement(bookingQuery)) {
                        bookingStatement.setInt(1, customer.getUserId());
                        bookingStatement.setInt(2, carId);
                        bookingStatement.setDate(3, Date.valueOf(startDate));
                        bookingStatement.setDate(4, Date.valueOf(endDate));
                        bookingStatement.setDouble(5, totalCost);
                        bookingStatement.executeUpdate();
                    }

                    // Update the car's availability to false
                    String updateQuery = "UPDATE cars SET is_available = false WHERE car_id = ?";
                    try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                        updateStatement.setInt(1, carId);
                        updateStatement.executeUpdate();
                    }

                    connection.commit();
                    System.out.println("Car booking successful! Total cost: " + totalCost);
                } else {
                    System.out.println("Car not available.");
                }
            }
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    System.err.println("Rollback failed: " + ex.getMessage());
                }
            }
            System.err.println("Error during car booking: " + e.getMessage());
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                } catch (SQLException ex) {
                    System.err.println("Failed to reset autoCommit: " + ex.getMessage());
                }
            }
        }
    }

    // Display all cars in the system
    private static void viewAllCars() {
        String query = "SELECT * FROM cars";
        try (Connection connection = databaseHandler.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            System.out.println("\n=== List of Cars ===");
            while (resultSet.next()) {
                System.out.printf("Car ID: %d, Make: %s, Model: %s, Year: %d, Rental Rate: %.2f, Available: %b%n",
                        resultSet.getInt("car_id"),
                        resultSet.getString("make"),
                        resultSet.getString("model"),
                        resultSet.getInt("year"),
                        resultSet.getDouble("rental_rate"),
                        resultSet.getBoolean("is_available"));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching cars: " + e.getMessage());
        }
    }

    // Check if a user exists based on user ID
    private static boolean userExists(int userId) {
        String query = "SELECT * FROM users WHERE user_id = ?";
        try (Connection connection = databaseHandler.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();

        } catch (SQLException e) {
            System.err.println("Error checking user existence: " + e.getMessage());
            return false;
        }
    }

    // Retrieve a user from the database based on user ID
    private static User getUserFromDatabase(int userId) {
        String query = "SELECT * FROM users WHERE user_id = ?";

        try (Connection connection = databaseHandler.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                User user = new User();
                user.setUserId(userId);
                user.setName(resultSet.getString("name"));
                user.setEmail(resultSet.getString("email"));
                user.setRole(resultSet.getString("role"));
                return user;
            }
        } catch (SQLException e) {
            System.err.println("Error fetching user: " + e.getMessage());
        }
        return null;
    }
}
