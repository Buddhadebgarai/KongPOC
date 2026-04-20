# Kong POC (Proof of Concept)

This project demonstrates a simple API Gateway setup using Kong with a Spring Boot microservice.

## Architecture

- **Kong API Gateway**: Acts as the entry point for API requests, routing them to the appropriate services.
- **Users Service**: A Spring Boot application providing user-related endpoints.

## Services

### Kong
- **Image**: kong:latest
- **Configuration**: Declarative config via `config/kong.yml`
- **Ports**:
  - 8000: Proxy port
  - 8001: Admin API
  - 8002: Manager UI

### Users Service
- **Framework**: Spring Boot 3.5.13
- **Java Version**: 25
- **Port**: 8080
- **Endpoint**: GET /api/v1/users/ - Returns a welcome message

## Getting Started

### Prerequisites
- Docker and Docker Compose
- Java 25 (for running the users service locally, if needed)
- Maven (for building the users service)

### Running the Project

1. **Start the Users Service**:
   ```bash
   cd services/users
   mvn spring-boot:run
   ```
   The service will start on port 8080.

2. **Start Kong**:
   ```bash
   docker-compose up -d
   ```
   Kong will start and load the configuration from `config/kong.yml`.

3. **Test the API**:
   - Direct access: `http://localhost:8080/api/v1/users/`
   - Via Kong: `http://localhost:8000/users`

### Kong Configuration

The Kong configuration in `config/kong.yml` defines:
- A service named `users-service` pointing to the Spring Boot app
- A route `/users` that proxies GET requests to the users service

## Development

### Building the Users Service
```bash
cd services/users
mvn clean package
```

### Kong Admin API
Access the Kong Admin API at `http://localhost:8001` to manage services, routes, and plugins.

### Kong Manager
Access the Kong Manager UI at `http://localhost:8002` for a web-based management interface.

## Project Structure
```
.
├── docker-compose.yaml          # Docker Compose configuration for Kong
├── config/
│   └── kong.yml                 # Kong declarative configuration
└── services/
    └── users/                   # Spring Boot users service
        ├── src/
        │   ├── main/
        │   │   ├── java/com/example/users/
        │   │   │   ├── UsersApplication.java
        │   │   │   └── controller/users.java
        │   │   └── resources/application.properties
        │   └── test/
        ├── pom.xml
        └── HELP.md
```

## Technologies Used
- Kong (API Gateway)
- Spring Boot (Microservice framework)
- Docker & Docker Compose (Containerization)
- Maven (Build tool)
- Java 25