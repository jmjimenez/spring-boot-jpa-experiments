# Spring Boot JPA Experiments

A Spring Boot application demonstrating modern Java development practices with JPA, hexagonal architecture, and comprehensive testing strategies.

## 🚀 Features

- **Spring Boot 3.5.3** with Java 21
- **Hexagonal Architecture** with clear separation of concerns
- **JPA/Hibernate** with PostgreSQL database
- **Flyway** for database migrations
- **Spring Shell** for CLI interactions
- **Swagger/OpenAPI** documentation
- **TestContainers** for integration testing
- **Comprehensive test suite** with unit and integration tests
- **Docker Compose** for local development
- **Spring Boot Actuator** for monitoring
- **HATEOAS** for REST API hypermedia support

## 📚 Included Libraries

### Core Dependencies
- **Spring Boot Starter Web** - Web application framework
- **Spring Boot Starter Data JPA** - JPA/Hibernate integration
- **Spring Boot Starter Validation** - Bean validation
- **Spring Boot Starter HATEOAS** - Hypermedia REST API support
- **Spring Boot DevTools** - Development utilities

### Database & Migration
- **PostgreSQL** - Primary database
- **Flyway Core** - Database migration tool
- **Flyway Database PostgreSQL** - PostgreSQL-specific migration support

### API Documentation
- **SpringDoc OpenAPI Starter WebMVC UI** - Swagger/OpenAPI documentation

### CLI & Utilities
- **Spring Shell Starter** - Command-line interface
- **ModelMapper** - Object mapping utility
- **Lombok** - Reduces boilerplate code

### Testing Dependencies
- **Spring Boot Starter Test** - Core testing framework
- **Spring Boot TestContainers** - Container-based testing
- **TestContainers JUnit Jupiter** - JUnit 5 integration
- **TestContainers PostgreSQL** - PostgreSQL container support
- **JUnit Platform Suite** - Test suite organization

## 🏗️ Project Structure

The project follows hexagonal architecture principles with clear separation between domain, application, and infrastructure layers:

```
src/main/java/es/jmjg/experiments/
├── ExperimentsApplication.java          # Main application class
├── domain/                             # Domain layer (business logic)
│   ├── entity/                         # Domain entities
│   ├── repository/                     # Repository interfaces
│   └── exception/                      # Domain exceptions
├── application/                        # Application layer (use cases)
│   ├── user/                          # User-related use cases
│   ├── post/                          # Post-related use cases
│   └── tag/                           # Tag-related use cases
└── infrastructure/                     # Infrastructure layer (external concerns)
    ├── controller/                     # REST controllers
    ├── repository/                     # Repository implementations
    └── config/                        # Configuration classes
```

### Test Structure
```
src/test/java/es/jmjg/experiments/
├── shared/                            # Shared test utilities
├── suite/                             # Test suites
├── application/                        # Application layer tests
└── infrastructure/                     # Infrastructure layer tests
```

## 🧪 Testing

The project includes a comprehensive testing strategy with separate profiles for different test types.

### Test Profiles

#### Unit Tests
```bash
mvn test -P unit-tests
```
- Runs only unit tests (excluding integration tests)
- Fast execution (30-second timeout)
- Uses mocked dependencies

#### Integration Tests
```bash
mvn test -P integration-tests
```
- Runs only integration tests
- Uses TestContainers for database testing
- Longer timeout (300 seconds)

#### All Tests
```bash
mvn test -P all-tests
```
- Runs both unit and integration tests
- Complete test coverage

#### Default Profile
```bash
mvn test
```
- Default profile runs all tests except test suites
- 300-second timeout for integration tests

### Test Execution Examples

```bash
# Run all tests (default profile)
mvn test

# Run only unit tests
mvn test -P unit-tests

# Run only integration tests
mvn test -P integration-tests

# Run all tests including test suites
mvn test -P all-tests

# Run tests with verbose output
mvn test -X

# Run specific test class
mvn test -Dtest=UserServiceTest

# Run tests with specific pattern
mvn test -Dtest=*ServiceTest
```

### Test Configuration

- **TestContainers**: PostgreSQL database for integration tests
- **Test Suites**: Organized test execution with JUnit Platform Suite
- **Shared Test Data**: Reusable test data and utilities
- **Profile-based Configuration**: Different database configurations per profile

## 🚀 Getting Started

### Prerequisites
- Java 21
- Maven 3.6+
- Docker (for TestContainers)

### Environment Configuration

The project uses environment variables for configuration. Follow these steps to set up your environment:

1. **Copy the environment template**:
   ```bash
   cp .env.example .env
   ```

2. **Expand the environment configuration**:
   The `.env.example` file contains a basic template. Edit the `.env` file and add the necessary environment variables for your local setup:
   ```bash
   # Application Configuration
   DEFAULT_PROFILE=dev
   SPRING_APPLICATION_NAME=experiments
   
   # Database Configuration
   DB_HOST=localhost
   DB_PORT=5432
   DB_NAME=blog
   DB_USERNAME=blog
   DB_PASSWORD=secret_password
   DB_URL=jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}
   
   # JPA Configuration
   JPA_DDL_AUTO=validate
   JPA_SHOW_SQL=false
   JPA_FORMAT_SQL=false
   
   # Flyway Configuration
   FLYWAY_ENABLED=true
   FLYWAY_LOCATIONS=classpath:db/migration,classpath:db/migration/dev
   
   # Swagger/OpenAPI Configuration
   SWAGGER_UI_PATH=/swagger-ui.html
   API_DOCS_PATH=/api-docs
   
   # Server Configuration
   SERVER_PORT=8080
   
   # Logging Configuration
   LOGGING_LEVEL_ROOT=INFO
   LOGGING_LEVEL_ES_JMJG_EXPERIMENTS=DEBUG
   
   # Actuator Configuration
   MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE=health,info,metrics,prometheus
   MANAGEMENT_ENDPOINT_HEALTH_SHOW-DETAILS=always
   ```

3. **Load environment variables**:
   The application will automatically load the `.env` file. Make sure your IDE or runtime environment is configured to use these variables.

**Note**: The `.env` file is already in `.gitignore` to prevent committing sensitive information to version control.

### Running the Application

**Run the application**:
```bash
mvn spring-boot:run
```

The application will automatically:
- Start the PostgreSQL database using Docker Compose
- Run database migrations with Flyway
- Start the Spring Boot application

**Access the application**:
- Application: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- API Docs: http://localhost:8080/api-docs
- Actuator: http://localhost:8080/actuator

**Note**: The PostgreSQL database is automatically managed by Spring Boot Docker Compose support. The database container will start when the application starts and stop when the application stops.

### Development

The application includes several development tools:

- **Spring Boot DevTools**: Automatic restart on code changes
- **Spring Shell**: Interactive CLI for testing features
- **Docker Compose**: Local PostgreSQL database
- **Flyway**: Automatic database migrations

### Database Migrations

The project uses Flyway for database migrations:

- **Versioned migrations**: Schema changes (run once)
- **Repeatable migrations**: Data seeding (run every time)
- **Profile-specific migrations**: Different data for dev/test environments

See `FLYWAY_README.md` for detailed migration information.

### API Documentation

The project includes comprehensive API documentation:

- **Swagger UI**: Interactive API documentation
- **OpenAPI Specification**: Machine-readable API spec
- **Automatic documentation**: Based on annotations

See `SWAGGER_README.md` for detailed documentation setup.

## 🔧 Configuration

Key configuration files:
- `application.properties`: Main application configuration
- `compose.yaml`: Docker Compose for local development
- `pom.xml`: Maven dependencies and build configuration

## 📝 Additional Documentation

- `FLYWAY_README.md`: Database migration setup and usage
- `SWAGGER_README.md`: API documentation configuration
- `.cursorrules`: Development guidelines and coding standards 