# ClusteredData Warehouse - FX Deals Management System

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.7-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![H2](https://img.shields.io/badge/H2-2.3.232-blue.svg)](https://www.h2database.com/)
[![Docker](https://img.shields.io/badge/Docker-Enabled-blue.svg)](https://www.docker.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

## Table of Contents
- [Overview](#overview)
- [Features](#features)
- [Architecture](#architecture)
- [Getting Started](#getting-started)
- [Database Schema](#database-schema)
- [API Documentation](#api-documentation)
- [Swagger UI](#swagger-ui)
- [Testing](#testing)
- [Configuration](#configuration)
- [Docker Deployment](#docker-deployment)
- [Error Handling](#error-handling)

## Overview

ClusteredData Warehouse is a robust data warehouse solution developed for ProgressSoft to analyze and persist FX (Foreign Exchange) deal transactions. The system accepts deal details from various sources and stores them in a database with comprehensive validation, error handling, and duplicate detection.

This application demonstrates enterprise-level Spring Boot development with best practices including proper layered architecture, comprehensive testing, API documentation with Swagger, and containerization support.

## Features

### Core Functionality
- **Deal Import**: Accept and persist FX deal details with comprehensive validation
- **Duplicate Prevention**: Prevent importing the same deal twice using unique constraints
- **No Rollback Policy**: Successfully imported deals are persisted regardless of subsequent failures
- **Batch Processing**: Import multiple deals simultaneously with individual success/failure tracking
- **RESTful API**: Well-documented REST endpoints for deal management
- **Interactive API Documentation**: Swagger UI for easy API exploration and testing

### Technical Features
- **Field Validation**: Comprehensive validation using Jakarta Bean Validation
- **ISO Currency Validation**: Validates currency codes against ISO 4217 standard
- **Exception Handling**: Global exception handler with proper error responses
- **Logging**: Detailed logging using SLF4J/Logback with configurable levels
- **Unit Testing**: Extensive test coverage (23 tests) using JUnit 5, Mockito, and AssertJ
- **Code Coverage**: JaCoCo integration for test coverage reporting
- **Docker Support**: Complete Docker Compose setup for MySQL deployment
- **H2 In-Memory Database**: Development-ready configuration without external dependencies
- **Database Indexing**: Optimized queries with proper indexing strategies
- **Pagination Support**: Efficient data retrieval with Spring Data JPA pagination

## Architecture

### Technology Stack
- **Backend Framework**: Spring Boot 3.5.7
- **Language**: Java 17
- **Database**: H2 In-Memory Database (MySQL 8.0 support via Docker)
- **ORM**: Spring Data JPA / Hibernate
- **Validation**: Jakarta Bean Validation
- **Build Tool**: Maven
- **Testing**: JUnit 5, Mockito, AssertJ
- **API Documentation**: Springdoc OpenAPI 3 (Swagger)
- **Containerization**: Docker & Docker Compose
- **Code Coverage**: JaCoCo
- **Logging**: SLF4J with Logback

### Project Structure
```
ProgressSoft/
├── src/
│   ├── main/
│   │   ├── java/org/example/progresssoft/
│   │   │   ├── entity/          # JPA entities
│   │   │   │   └── FxDeal.java
│   │   │   ├── repository/      # Data access layer
│   │   │   │   └── FxDealRepository.java
│   │   │   ├── service/         # Business logic
│   │   │   │   └── FxDealService.java
│   │   │   ├── controller/      # REST controllers
│   │   │   │   └── FxDealController.java
│   │   │   ├── config/          # Configuration classes
│   │   │   │   └── OpenApiConfig.java
│   │   │   ├── dto/             # Data Transfer Objects
│   │   │   │   ├── FxDealRequest.java
│   │   │   │   └── FxDealResponse.java
│   │   │   └── exception/       # Exception handling
│   │   │       ├── DuplicateDealException.java
│   │   │       ├── InvalidDealException.java
│   │   │       ├── ErrorResponse.java
│   │   │       └── GlobalExceptionHandler.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       ├── java/org/example/progresssoft/
│       │   ├── service/         # Service tests
│       │   ├── controller/      # Controller tests
│       │   └── repository/      # Repository tests
│       └── resources/
│           └── application.properties
├── Dockerfile
├── docker-compose.yml
├── init-db.sql
├── sample-deals.json
├── FX-Deals-Postman-Collection.json
├── Makefile
├── pom.xml
└── README.md
```

## Getting Started

### Prerequisites
- **Java 17** or higher
- **Maven 3.8+** (or use included Maven wrapper)
- **Docker & Docker Compose** (optional, for MySQL deployment)

Note: The application is configured to use H2 in-memory database by default, which requires no external database installation. For production deployment with MySQL, Docker Compose is available.

### Installation

#### Option 1: Quick Start with H2 Database (Recommended for Development)

1. **Clone the repository**
```bash
git clone <repository-url>
cd ProgressSoft
```

2. **Run the application**
```bash
./mvnw spring-boot:run
```
Or on Windows:
```cmd
mvnw.cmd spring-boot:run
```

3. **Verify the application is running**
```bash
curl http://localhost:8080/api/fx-deals/health
```

4. **Access Swagger UI**

Open your browser and navigate to:
```
http://localhost:8080/swagger-ui.html
```

#### Option 2: Using Docker with MySQL (Production)

1. **Clone the repository**
```bash
git clone <repository-url>
cd ProgressSoft
```

2. **Build and run using Docker Compose**
```bash
make docker-up
```
Or manually:
```bash
docker-compose up -d
```

3. **Verify the application is running**
```bash
curl http://localhost:8080/api/fx-deals/health
```

Note: Docker Compose sets up both the Spring Boot application and MySQL database. The database is automatically initialized with the schema.

## Database Schema

### FX_DEALS Table
```sql
CREATE TABLE fx_deals (
    id                      BIGSERIAL PRIMARY KEY,
    deal_unique_id          VARCHAR(100) NOT NULL UNIQUE,
    from_currency_iso_code  VARCHAR(3) NOT NULL,
    to_currency_iso_code    VARCHAR(3) NOT NULL,
    deal_timestamp          TIMESTAMP NOT NULL,
    deal_amount             DECIMAL(19, 4) NOT NULL,
    created_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_deal_unique_id ON fx_deals(deal_unique_id);
CREATE INDEX idx_deal_timestamp ON fx_deals(deal_timestamp);
```

## Swagger UI

### Interactive API Documentation

The application includes Swagger UI for interactive API documentation and testing.

**Access Swagger UI:**
```
http://localhost:8080/swagger-ui.html
```

**OpenAPI JSON Specification:**
```
http://localhost:8080/api-docs
```

### Swagger Features
- Interactive API exploration
- Try out API endpoints directly from the browser
- Request/response schemas with examples
- Authentication testing
- Export API specifications

## API Documentation

### Base URL
```
http://localhost:8080/api/fx-deals
```

### Endpoints

#### 1. Import Single Deal
**POST** `/api/fx-deals`

**Request Body:**
```json
{
  "dealUniqueId": "DEAL-12345",
  "fromCurrencyIsoCode": "USD",
  "toCurrencyIsoCode": "EUR",
  "dealTimestamp": "2024-11-10T10:30:00",
  "dealAmount": 10000.50
}
```

**Success Response (201 Created):**
```json
{
  "id": 1,
  "dealUniqueId": "DEAL-12345",
  "fromCurrencyIsoCode": "USD",
  "toCurrencyIsoCode": "EUR",
  "dealTimestamp": "2024-11-10T10:30:00",
  "dealAmount": 10000.50,
  "createdAt": "2024-11-10T10:35:00"
}
```

**Error Response (409 Conflict - Duplicate):**
```json
{
  "timestamp": "2024-11-10T10:35:00",
  "status": 409,
  "error": "Duplicate Deal",
  "message": "Deal with ID 'DEAL-12345' already exists in the system",
  "path": "/api/fx-deals"
}
```

#### 2. Import Batch Deals
**POST** `/api/fx-deals/batch`

**Request Body:**
```json
[
  {
    "dealUniqueId": "DEAL-001",
    "fromCurrencyIsoCode": "USD",
    "toCurrencyIsoCode": "EUR",
    "dealTimestamp": "2024-11-10T10:30:00",
    "dealAmount": 10000.50
  },
  {
    "dealUniqueId": "DEAL-002",
    "fromCurrencyIsoCode": "GBP",
    "toCurrencyIsoCode": "JPY",
    "dealTimestamp": "2024-11-10T11:45:00",
    "dealAmount": 5000.75
  }
]
```

**Response (207 Multi-Status):**
```json
{
  "totalReceived": 2,
  "successCount": 2,
  "failureCount": 0,
  "results": [
    {
      "dealUniqueId": "DEAL-001",
      "success": true,
      "message": "Deal imported successfully",
      "data": { /* deal response */ }
    },
    {
      "dealUniqueId": "DEAL-002",
      "success": true,
      "message": "Deal imported successfully",
      "data": { /* deal response */ }
    }
  ]
}
```

#### 3. Get All Deals
**GET** `/api/fx-deals?page=0&size=20&sortBy=createdAt&sortDir=desc`

**Success Response (200 OK):**
```json
{
  "content": [
    {
      "id": 1,
      "dealUniqueId": "DEAL-001",
      "fromCurrencyIsoCode": "USD",
      "toCurrencyIsoCode": "EUR",
      "dealTimestamp": "2024-11-10T10:30:00",
      "dealAmount": 10000.50,
      "createdAt": "2024-11-10T10:35:00"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20
  },
  "totalElements": 1,
  "totalPages": 1
}
```

#### 4. Get Deal by Unique ID
**GET** `/api/fx-deals/{dealUniqueId}`

**Success Response (200 OK):**
```json
{
  "id": 1,
  "dealUniqueId": "DEAL-12345",
  "fromCurrencyIsoCode": "USD",
  "toCurrencyIsoCode": "EUR",
  "dealTimestamp": "2024-11-10T10:30:00",
  "dealAmount": 10000.50,
  "createdAt": "2024-11-10T10:35:00"
}
```

#### 5. Health Check
**GET** `/api/fx-deals/health`

**Success Response (200 OK):**
```json
{
  "status": "UP",
  "service": "FX Deals Warehouse"
}
```

## Testing

### Run All Tests
```bash
make test
```
Or:
```bash
./mvnw test
```

### Run Tests with Coverage
```bash
make coverage
```
Or:
```bash
./mvnw clean test jacoco:report
```

View coverage report at: `target/site/jacoco/index.html`

### Test Coverage
The application includes comprehensive unit tests:
- **Service Layer Tests**: Business logic and validation
- **Controller Layer Tests**: API endpoints and error handling
- **Repository Layer Tests**: Data access operations

## Validation Rules

### Request Field Validations

| Field | Validation Rules |
|-------|-----------------|
| `dealUniqueId` | Required, Max 100 characters, Must be unique |
| `fromCurrencyIsoCode` | Required, Must be valid 3-letter ISO 4217 code |
| `toCurrencyIsoCode` | Required, Must be valid 3-letter ISO 4217 code |
| `dealTimestamp` | Required, Cannot be in the future |
| `dealAmount` | Required, Must be > 0, Max 15 integer + 4 decimal digits |

### Business Validations
- Currency codes must be valid ISO 4217 codes (e.g., USD, EUR, GBP)
- From and To currencies must be different
- No duplicate deals (based on `dealUniqueId`)
- Deal timestamp cannot be in the future
- Deal amount must be positive

## Configuration

### Application Properties
Key configurations in `application.properties`:

```properties
# Server Configuration
server.port=8080

# H2 Database Configuration (Default)
spring.datasource.url=jdbc:h2:mem:fxdeals_db
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# H2 Console (Development)
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# JPA Configuration
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=update

# Swagger/OpenAPI Configuration
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.operationsSorter=method
springdoc.swagger-ui.tagsSorter=alpha

# Logging Configuration
logging.level.org.example.progresssoft=DEBUG
```

### H2 Console Access

For development, you can access the H2 database console:

**URL**: http://localhost:8080/h2-console

**Connection Details**:
- JDBC URL: `jdbc:h2:mem:fxdeals_db`
- Username: `sa`
- Password: (leave empty)

### Environment Variables (Docker with MySQL)
```yaml
SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/fxdeals_db
SPRING_DATASOURCE_USERNAME: root
SPRING_DATASOURCE_PASSWORD: root
SPRING_JPA_HIBERNATE_DDL_AUTO: update
SPRING_JPA_DATABASE_PLATFORM: org.hibernate.dialect.MySQLDialect
```

## Makefile Commands

```bash
make help           # Show all available commands
make install        # Install dependencies
make build          # Build the application
make test           # Run unit tests
make coverage       # Run tests with coverage
make run            # Run application locally
make clean          # Clean build artifacts
make docker-build   # Build Docker image
make docker-up      # Start Docker containers
make docker-down    # Stop Docker containers
make docker-logs    # View Docker logs
make all            # Complete workflow
```

## Sample Data and Testing

The application includes sample data that is automatically loaded when using Docker:
- 5 sample FX deals are pre-loaded in `init-db.sql`
- Additional sample deals available in `sample-deals.json`

### Testing with Sample Data

Import sample deals using curl:
```bash
curl -X POST http://localhost:8080/api/fx-deals/batch \
  -H "Content-Type: application/json" \
  -d @sample-deals.json
```

### Postman Collection

A complete Postman collection is included for API testing:

**File**: `FX-Deals-Postman-Collection.json`

**Import Instructions**:
1. Open Postman
2. Click "Import" button
3. Select the `FX-Deals-Postman-Collection.json` file
4. Collection includes 17 pre-configured requests:
   - Health check
   - Single deal import (success and error cases)
   - Batch deal import
   - Get all deals with pagination
   - Get deal by unique ID
   - All validation error scenarios

## Error Handling

The application provides comprehensive error handling:

### Error Response Format
```json
{
  "timestamp": "2024-11-10T10:35:00",
  "status": 400,
  "error": "Validation Failed",
  "message": "Invalid input data",
  "path": "/api/fx-deals",
  "validationErrors": {
    "dealUniqueId": "Deal Unique ID is required",
    "dealAmount": "Deal amount must be greater than 0"
  }
}
```

### HTTP Status Codes
- **201 Created**: Deal successfully imported
- **200 OK**: Successful retrieval
- **207 Multi-Status**: Batch import with mixed results
- **400 Bad Request**: Validation error
- **409 Conflict**: Duplicate deal
- **500 Internal Server Error**: Unexpected error

## Logging

Logs are configured with multiple levels:
- **INFO**: General application flow
- **DEBUG**: Detailed business logic execution
- **WARN**: Duplicate deal attempts
- **ERROR**: Validation failures and exceptions

Logs are stored in: `logs/fx-deals-warehouse.log`

## Docker Deployment

### Build and Deploy
```bash
# Build the image
docker-compose build

# Start services
docker-compose up -d

# View logs
docker-compose logs -f app

# Stop services
docker-compose down
```

### Services
- **app**: Spring Boot application (port 8080)
- **mysql**: MySQL database (port 3306)

Note: The Docker Compose setup uses MySQL. For local development without Docker, the application defaults to H2 in-memory database.

## Additional Resources

### H2 Database
- **Console**: http://localhost:8080/h2-console
- **JDBC URL**: jdbc:h2:mem:fxdeals_db
- In-memory database (data resets on application restart)
- Perfect for development and testing

### Swagger/OpenAPI
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs JSON**: http://localhost:8080/api-docs
- Interactive API documentation with try-it-out functionality

### Postman Collection
- **File**: FX-Deals-Postman-Collection.json
- 17 pre-configured API test requests
- Covers all endpoints and error scenarios

## Application Endpoints Summary

### API Endpoints
- POST `/api/fx-deals` - Import single deal
- POST `/api/fx-deals/batch` - Import multiple deals
- GET `/api/fx-deals` - Get all deals (with pagination)
- GET `/api/fx-deals/{dealUniqueId}` - Get specific deal
- GET `/api/fx-deals/health` - Health check

### Documentation & Tools
- GET `/swagger-ui.html` - Interactive API documentation
- GET `/api-docs` - OpenAPI specification (JSON)
- GET `/h2-console` - H2 database console (development)

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License.

## Author

Developed for ProgressSoft Corporation - Java Developer Assessment

## Support

For questions or issues, please contact the development team.

---

**Note**: This application is developed as part of the ProgressSoft Java Developer assessment and demonstrates best practices in Spring Boot development, including:
- Proper layered architecture (Controller, Service, Repository)
- Comprehensive validation and error handling
- Extensive unit testing with high coverage
- Interactive API documentation with Swagger
- Support for multiple database platforms (H2, MySQL)
- Docker containerization for easy deployment
- RESTful API design principles
- Professional logging and monitoring
