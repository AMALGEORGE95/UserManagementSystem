# User Management System

A Spring Boot application for managing users with features like registration, login, and role-based access control. The application is secured using Basic Authentication and supports Docker for easy deployment.

## Features

### User Registration:

- Register a new user with details like name, email, gender, and password.
- Email must be unique.

### User Login:

- Validate user credentials using Basic Authentication.

### Role-Based Access Control:

- Only ADMIN users can access the `/api/user/all` and `/api/user/delete/{email}` endpoints.

### Fetch All Users:

- Retrieve a list of all registered users (accessible only by ADMIN).

### Delete User by Email:

- Delete a user by their email (accessible only by ADMIN).

### Docker Support:

- The application can be run locally using Docker and Docker Compose.

## Technologies Used

- **Spring Boot**: Backend framework.
- **Spring Security**: For authentication and authorization.
- **Spring Data JPA**: For database operations.
- **PostgreSQL**: Database for storing user information.
- **Docker**: Containerization for easy deployment.
- **JUnit 5 & Mockito**: For unit testing the service layer.
- **Swagger**: API documentation.

## Prerequisites

- Java 17 or higher
- Maven
- Docker
- PostgreSQL (if not using Docker)

## Setup Instructions

### 1. Clone the Repository

```bash
git clone https://github.com/AMALGEORGE95/UserManagementSystem.git
cd user-management-system
```

### 2. Build the Application

Run the following command to build the application:

```bash
mvn clean package
```

This will generate a JAR file in the `target` directory.

### 3. Run the Application Locally

#### Without Docker

Ensure PostgreSQL is running locally.

Update the `application.properties` file with your database credentials:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/user_management_db
spring.datasource.username=postgres
spring.datasource.password=root
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

Run the application:

```bash
mvn spring-boot:run
```

#### With Docker

Build the Docker image:

```bash
docker build -t user-management-system .
```

Run the Docker container:

```bash
docker run -p 8080:8080 user-management-system
```

### 4. Run with Docker Compose

If you want to run the application along with a PostgreSQL database, use Docker Compose:

Create a `docker-compose.yml` file (if not already present):

```yaml
version: '3.8'

services:
  app:
    image: user-management-system
    build:
      context: .
      dockerfile: Dockerfile
    ports:
      - "8080:8080"
    environment:
      - SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/user
      - SPRING_DATASOURCE_USERNAME=postgres
      - SPRING_DATASOURCE_PASSWORD=root
    depends_on:
      - db

  db:
    image: postgres:13
    environment:
      POSTGRES_DB: user
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: root
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

volumes:
  postgres_data:
```

Start the application and database:

```bash
docker-compose up
```

Access the application at:

```
http://localhost:8080
```

## API Documentation

The application uses Swagger for API documentation. After running the application, access the Swagger UI at:

```
http://localhost:8080/swagger-ui.html
```

## Testing

### Unit Tests

Unit tests for the service layer are written using JUnit 5 and Mockito. Run the tests using:

```bash
mvn test
```

## Default Users

**Admin User:**

- Username: `admin`
- Password: `admin123`
- Role: `ADMIN`


## Endpoints

| HTTP Method | Endpoint                   | Description              | Access     |
| ----------- | -------------------------- |--------------------------| ---------- |
| POST        | `/api/user/register`       | Register a new user      | Public     |
| POST        | `/api/auth/login`          | User login               | Public     |
| GET         | `/api/user/all`            | Get all registered users | ADMIN only |
| DELETE      | `/api/user/delete/{email}` | Delete a user by email   | ADMIN only |

## Troubleshooting

### Docker Container Fails to Start:

- Ensure no other application is using port `8080` or `5432`.
- Check the logs using `docker logs <container_id>`.

### Database Connection Issues:

- Verify the database credentials in `application.properties` or `docker-compose.yml`.
- Ensure PostgreSQL is running.

### Swagger UI Not Loading:

- Ensure the application is running and accessible at `http://localhost:8080`.

##

## Author

- **Amal George**
- [**amalgeorge95@gmail.vom**](mailto:amalgeorge95@gmail.vomYour)
- **AMALGEORGE95**

