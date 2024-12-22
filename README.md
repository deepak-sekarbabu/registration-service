# Registration Service for Healthcare Appointments

The Registration Service is a Spring Boot application that manages user registration and appointment scheduling for healthcare services.

This service provides a robust API for handling user accounts and medical appointments. It offers functionalities such as user registration, appointment booking, and appointment management for patients, doctors, and clinics. The application is built using reactive programming principles, ensuring high performance and scalability.

Key features include:
- User management (create, read, update, delete)
- Appointment scheduling and management
- Slot availability checking
- Queue management for appointments
- Cross-Origin Resource Sharing (CORS) support
- Comprehensive error handling
- API documentation with Swagger/OpenAPI

## Repository Structure

- `src/main/java/com/deepak/registrationservice/`: Root package for the application
  - `config/`: Configuration classes (e.g., CORS configuration)
  - `controller/`: REST controllers for handling HTTP requests
  - `exception/`: Custom exception classes and global exception handler
  - `model/`: Data models for users and appointments
  - `repository/`: Spring Data R2DBC repositories for database operations
  - `service/`: Service layer for business logic
  - `validation/`: Custom validation annotations and validators
- `src/test/java/com/deepak/registrationservice/`: Test classes
- `pom.xml`: Maven project configuration file

Key Files:
- `RegistrationServiceApplication.java`: Main entry point of the application
- `AppointmentController.java`: Handles appointment-related API endpoints
- `UserController.java`: Handles user-related API endpoints
- `AppointmentServiceImpl.java`: Implements appointment business logic
- `GlobalExceptionHandler.java`: Centralized exception handling for the application

## Usage Instructions

### Installation

Prerequisites:
- Java 21 or later
- Maven 3.6 or later
- MySQL database (for R2DBC connectivity)

Steps:
1. Clone the repository
2. Navigate to the project root directory
3. Run `mvn clean install` to build the project and run tests

### Configuration

1. Configure the database connection in `application.properties`:
   ```
   spring.r2dbc.url=r2dbc:mysql://localhost:3306/your_database_name
   spring.r2dbc.username=your_username
   spring.r2dbc.password=your_password
   ```

2. Configure CORS settings in `CorsConfig.java` if needed

### Running the Application

To start the application, run:
```
mvn spring-boot:run
```

The application will start on `http://localhost:8080` by default.

### API Usage

The service provides RESTful APIs for user and appointment management. Here are some example endpoints:

1. Create a new user:
   ```
   POST /v1/user
   Content-Type: application/json

   {
     "name": "John Doe",
     "phoneNumber": "+911234567890",
     "email": "john.doe@example.com",
     "birthdate": "1990-01-01"
   }
   ```

2. Create a new appointment:
   ```
   POST /v1/appointment
   Content-Type: application/json

   {
     "userId": 1,
     "appointmentType": "GENERAL_CHECKUP",
     "appointmentFor": "SELF",
     "appointmentDate": "2023-06-01T10:00:00",
     "symptom": "FEVER",
     "slotId": 1,
     "doctorId": "DOC001",
     "clinicId": 1
   }
   ```

3. Retrieve all appointments:
   ```
   GET /v1/appointments?page=0&size=10
   ```

For a complete list of available endpoints and their usage, refer to the Swagger documentation available at `http://localhost:8080/swagger-ui.html` when the application is running.

### Testing

To run the tests, execute:
```
mvn test
```

### Troubleshooting

1. Database Connection Issues:
   - Ensure the MySQL server is running and accessible
   - Verify the database credentials in `application.properties`
   - Check for any firewall restrictions

2. CORS Errors:
   - Review and update the CORS configuration in `CorsConfig.java`
   - Ensure the client application's origin is included in the allowed origins

3. Appointment Booking Failures:
   - Verify that the requested slot is available
   - Check for any conflicting appointments in the database
   - Ensure all required fields are provided in the request payload

For more detailed logging, you can enable debug mode by adding the following to `application.properties`:
```
logging.level.com.deepak.registrationservice=DEBUG
```

## Data Flow

The Registration Service follows a typical flow for handling requests:

1. Client sends an HTTP request to a specific endpoint
2. The request is received by the appropriate controller (UserController or AppointmentController)
3. The controller delegates the business logic to the service layer (e.g., AppointmentServiceImpl)
4. The service layer interacts with the repository layer for data persistence
5. The repository layer communicates with the database using Spring Data R2DBC
6. Results are passed back up the chain to the controller
7. The controller returns an HTTP response to the client

```
Client <-> Controller <-> Service <-> Repository <-> Database
```

Important technical considerations:
- The application uses reactive programming with Project Reactor, allowing for non-blocking I/O operations
- CORS is configured to allow requests from specific origins
- Global exception handling is implemented to provide consistent error responses
- Custom validation is used for date fields

## Authors

- [@deepak-sekarbabu](https://github.com/deepak-sekarbabu)

## License

# Restricted Usage License

This repository is protected by a Restricted Usage License. No part of the content within this repository may be used,
reproduced, distributed, or modified in any form without prior written permission from the owner.

For inquiries regarding the use of this repository or its contents, please
contact [Deepak Sekarbabu/deepakinmail@gmail.com].

Unauthorized use of this repository or its contents may result in legal action.

Thank you for respecting the terms of this license.

