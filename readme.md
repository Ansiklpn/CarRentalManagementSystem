```markdown
# Car Rental Management System

The Car Rental Management System is a Java-based backend application that demonstrates core Object-Oriented Programming principles alongside robust transaction handling using JDBC. It connects to a PostgreSQL database to manage users, a fleet of cars, and the booking process. The system supports two roles: **Admin** and **Customer**. Admin users can manage the car inventory and view statistics, while customers can view available cars and make bookings.

## Table of Contents

- [Features](#features)
- [Requirements](#requirements)
- [Installation](#installation)
- [Database Setup](#database-setup)
- [Running the Application](#running-the-application)
- [Project Structure](#project-structure)
- [Usage](#usage)
- [Troubleshooting](#troubleshooting)
- [Contributing](#contributing)
- [License](#license)

## Features

- **User Management**
  - Register new users (both Admin and Customer).
  - User login with role-based access.
- **Admin Capabilities**
  - View all cars in the fleet.
  - Add, update, or delete car records.
  - View system statistics (total cars, booked cars, total bookings).
- **Customer Capabilities**
  - View available cars.
  - Book a car with proper transaction handling (ensuring atomicity between inserting the booking record and updating the car's availability).
- **Robust Error Handling**
  - Comprehensive exception handling for database connectivity and operations.
  - Transaction management to ensure data consistency.
- **Database Integration**
  - PostgreSQL is used for data persistence.
  - JDBC is used for all database interactions.

## Requirements

- **Java Development Kit (JDK 8 or later)**
- **PostgreSQL** (Ensure that PostgreSQL is installed and running)
- **PostgreSQL JDBC Driver** (e.g., `postgresql-42.x.x.jar`; include it in your classpath)
- **Git** (for cloning the repository)

## Installation

1. **Clone the Repository**

   ```bash
   git clone https://github.com/Ansiklpn/CarRentalManagementSystem
   cd CarRentalManagementSystem
   ```

2. **Configure the Database**

   - Create a PostgreSQL database named `carrentaldb`.
   - Update the credentials in the file `src/database/DatabaseHandler.java` (e.g., update the URL, USER, and PASSWORD constants).

3. **Set Up the Database Schema**

   Execute the SQL script located in the `sql/` directory to create the necessary tables. You can use `psql` or any PostgreSQL client to run the following script:

   ```

   -- Create the users table
   CREATE TABLE IF NOT EXISTS users (
       user_id INT PRIMARY KEY,
       name VARCHAR(100) NOT NULL,
       email VARCHAR(100) UNIQUE NOT NULL,
       role VARCHAR(50) NOT NULL
   );

   -- Create the cars table
   CREATE TABLE IF NOT EXISTS cars (
       car_id SERIAL PRIMARY KEY,
       make VARCHAR(50) NOT NULL,
       model VARCHAR(50) NOT NULL,
       year INT NOT NULL,
       rental_rate NUMERIC(10,2) NOT NULL,
       is_available BOOLEAN DEFAULT TRUE
   );

   -- Create the bookings table
   CREATE TABLE IF NOT EXISTS bookings (
       booking_id SERIAL PRIMARY KEY,
       user_id INT NOT NULL,
       car_id INT NOT NULL,
       start_date DATE NOT NULL,
       end_date DATE NOT NULL,
       total_cost NUMERIC(10,2),
       CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(user_id),
       CONSTRAINT fk_car FOREIGN KEY (car_id) REFERENCES cars(car_id)
   );
   ```

   **Note:** If you encounter permission issues (e.g., with sequences), you might need to grant privileges to your PostgreSQL user. For example:

   ```
   GRANT USAGE, SELECT ON SEQUENCE bookings_booking_id_seq TO carrental_user;

CarRentalManagementSystem/
├── src/
│   ├── postgresql-42.7.5 (1).jar	  # PostgreSQL JDBC Driver
│   ├── CarRentalSystem.java          # Main application file
│   ├── DatabaseHandler.java     # Handles PostgreSQL database connectivity
│   └── models/
│        ├── User.java                # User model class
│        ├── Car.java                 # Car model class
│        └── Booking.java             # Booking model class (includes transaction support)
├── utils/
│   └── VehicleFactory.java           # VehicleFactory class

```

## Usage

- **User Registration & Login:**
  - Users can register by providing a user ID, name, email, and role (Admin or Customer).
  - Admins are required to provide an admin registration password (`admin123` by default for demonstration).
  - After registration, users can log in using their user ID.
- **Admin Functions:**
  - View all cars in the fleet.
  - Add new cars.
  - Update or delete existing cars.
  - View system statistics (e.g., total number of cars, booked cars, and total bookings).
- **Customer Functions:**
  - View a list of available cars.
  - Book a car by selecting the car ID and specifying the number of rental days.
  - The booking process is handled within a transaction to ensure that both the booking record is inserted and the car's availability is updated atomically.
- **Transaction Management:**
  - If any error occurs during the booking process, the system rolls back all changes to maintain data consistency.

## Troubleshooting

- **Permission Issues:**
  - If you encounter errors like "нет доступа к таблице bookings" or sequence access errors, verify that the PostgreSQL user (configured in `DatabaseHandler.java`) has the appropriate privileges on the tables and sequences. Use `GRANT` commands as needed.
- **Database Connection Issues:**
  - Verify that your database credentials and URL in `DatabaseHandler.java` are correct.
  - Ensure that PostgreSQL is running and accessible at the specified URL.
- **Compilation and Runtime Errors:**
  - Ensure that the PostgreSQL JDBC driver is added to your classpath.
  - Review error messages in the console for further details.

## Contributing

Contributions to the Car Rental Management System are welcome! If you have suggestions for improvements, bug fixes, or new features, please fork the repository and submit a pull request. For major changes, please open an issue first to discuss what you would like to change.
