package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseHandler {
    private static DatabaseHandler instance;
    private Connection connection;

    // Updated URL, USER, and PASSWORD for the Car Rental Management System database
    private static final String URL = "jdbc:postgresql://localhost:5432/carrentaldb";
    private static final String USER = "carrental_user";
    private static final String PASSWORD = "your_password"; // Replace with your actual password

    static {
        try {
            Class.forName("org.postgresql.Driver"); // Ensure the PostgreSQL JDBC driver is present
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("PostgreSQL JDBC Driver Not Found!", e);
        }
    }

    public DatabaseHandler() {
        connect();
    }

    private void connect() {
        try {
            System.out.println("Attempting to connect to the Car Rental Management System database...");
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Database connected successfully!");
        } catch (SQLException e) {
            System.err.println("ERROR: Failed to connect to the Car Rental Management System database!");
            e.printStackTrace();
        }
    }

    public static synchronized DatabaseHandler getInstance() {
        if (instance == null) {
            instance = new DatabaseHandler();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connect();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to reconnect to the database!", e);
        }
        return connection;
    }
}
