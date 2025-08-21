# Spring Boot JPA Experiments

A Spring Boot application demonstrating modern Java development practices with JPA, hexagonal architecture, and comprehensive testing strategies.

## 🚀 Features

- **Spring Boot 3.5.3** with Java 21
- **Hexagonal Architecture** with clear separation of concerns
- **JPA/Hibernate** with PostgreSQL database
- **Flyway** for database migrations
- **Spring Shell** for CLI interactions
- **Swagger/OpenAPI** documentation
- **JWT Security** with Bearer token authentication
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

### Security
- **Spring Boot Starter Security** - Security framework
- **Auth0 Java JWT** - JWT token generation and validation

### API Documentation
- **SpringDoc OpenAPI Starter WebMVC UI** - Swagger/OpenAPI documentation

### CLI & Utilities
- **Spring Shell Starter** - Command-line interface
- **ModelMapper** - Object mapping utility
- **Lombok** - Reduces boilerplate code

### Monitoring & Observability
- **Spring Boot Actuator** - Application monitoring and metrics
- **Micrometer Prometheus** - Metrics collection and export

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
    ├── config/                        # Configuration classes
    └── security/                      # Security configuration
```

### Test Structure
```
src/test/java/es/jmjg/experiments/
├── shared/                            # Shared test utilities
├── suite/                             # Test suites
├── application/                        # Application layer tests
└── infrastructure/                     # Infrastructure layer tests
```

## 🗄️ Database Schema

The database uses PostgreSQL and follows a blog-like structure with users, posts, and tags. The schema is managed through Flyway migrations and includes comprehensive sample data for development and testing.

### Core Tables

#### 1. Users Table
```sql
CREATE TABLE Users (
    id SERIAL PRIMARY KEY,
    uuid UUID NOT NULL UNIQUE,
    name varchar(100) NOT NULL,
    email varchar(100) NOT NULL UNIQUE,
    username varchar(50) UNIQUE,
    password varchar(255) NOT NULL
);
```

**Purpose**: Stores user information for the blog system.

**Fields**:
- `id`: Auto-incrementing primary key
- `uuid`: Unique identifier for external API usage
- `name`: User's full name (required)
- `email`: User's email address (required, unique)
- `username`: Optional username (unique if provided)
- `password`: Encrypted password (required for JWT authentication)

#### 2. Post Table
```sql
CREATE TABLE Post (
    id SERIAL PRIMARY KEY,
    uuid UUID NOT NULL UNIQUE,
    user_id INT NOT NULL,
    title varchar(250) NOT NULL UNIQUE,
    body text NOT NULL,
    FOREIGN KEY (user_id) REFERENCES Users (id) ON DELETE CASCADE
);
```

**Purpose**: Stores blog posts created by users.

**Fields**:
- `id`: Auto-incrementing primary key
- `uuid`: Unique identifier for external API usage
- `user_id`: Foreign key to Users table (required)
- `title`: Post title (required, unique)
- `body`: Post content (required, text field)

#### 3. Tag Table
```sql
CREATE TABLE tag (
    id SERIAL PRIMARY KEY,
    uuid UUID NOT NULL UNIQUE,
    tag varchar(60) NOT NULL UNIQUE
);
```

**Purpose**: Stores tags that can be associated with posts and users.

### Junction Tables

#### 4. post_tag Table
```sql
CREATE TABLE post_tag (
    post_id INT NOT NULL,
    tag_id INT NOT NULL,
    PRIMARY KEY (post_id, tag_id),
    FOREIGN KEY (post_id) REFERENCES Post (id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tag (id) ON DELETE CASCADE
);
```

#### 5. user_tag Table
```sql
CREATE TABLE user_tag (
    user_id INT NOT NULL,
    tag_id INT NOT NULL,
    PRIMARY KEY (user_id, tag_id),
    FOREIGN KEY (user_id) REFERENCES Users (id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tag (id) ON DELETE CASCADE
);
```

### Entity Relationships

- **User Entity**: One-to-Many with Post, Many-to-Many with Tag
- **Post Entity**: Many-to-One with User, Many-to-Many with Tag
- **Tag Entity**: Many-to-Many with User, Many-to-Many with Post

## 🔐 JWT Security Implementation

The application includes JWT (JSON Web Token) authentication following Spring Security best practices.

### Authentication Flow

1. **Get a Token**: Send credentials to `/authenticate` endpoint
2. **Use the Token**: Include the token in the `Authorization` header for protected endpoints
3. **Token Expiration**: Tokens expire after 30 minutes and need to be renewed

### API Endpoints

#### Public Endpoints (No Authentication Required)
- `GET /` - Application root
- `POST /authenticate` - Authentication endpoint
- `GET /api-docs/**` - OpenAPI documentation
- `GET /swagger-ui/**` - Swagger UI

#### Protected Endpoints (Authentication Required)
- `GET /api/**` - All API endpoints require authentication

### Usage Examples

#### 1. Authenticate to Get a Token

```bash
curl -X POST http://localhost:8080/authenticate \
  -H "Content-Type: application/json" \
  -d '{
    "login": "leanne_graham",
    "password": "password"
  }'
```

**Response:**
```json
{
  "accessToken": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9..."
}
```

#### 2. Use the Token to Access Protected Endpoints

```bash
curl -X GET http://localhost:8080/api/users \
  -H "Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9..."
```

### Test Users

The application includes test users with the following credentials:

| Username | Password | Email |
|----------|----------|-------|
| `leanne_graham` | `password` | `leanne.graham@example.com` |
| `ervin_howell` | `password` | `ervin.howell@example.com` |
| `clementine_bauch` | `password` | `clementine.bauch@example.com` |
| `patricia_lebsack` | `password` | `patricia.lebsack@example.com` |
| `chelsey_dietrich` | `password` | `chelsey.dietrich@example.com` |

### Security Features

- **Password Encryption**: All passwords are encrypted using BCrypt
- **Token Signing**: JWT tokens are signed using HMAC512 algorithm with Auth0 Java JWT library
- **Stateless**: No server-side session storage
- **Role-based Access**: All authenticated users have `ROLE_USER` authority
- **CSRF Protection**: Disabled for REST API (not needed with JWT)
- **CORS**: Disabled (configure as needed for your frontend)
- **Token Expiration**: 30-minute token validity period

## 📖 Swagger/OpenAPI Documentation

The project includes comprehensive API documentation with Swagger/OpenAPI.

### Accessing Swagger UI

Once you start the application, you can access the Swagger UI at:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs

### Features

- **Interactive API Documentation**: Browse and test all API endpoints directly from the browser
- **Request/Response Examples**: See example requests and responses for each endpoint
- **OAuth2 Password Flow**: Built-in authentication form for requesting JWT tokens
- **JWT Bearer Token Authentication**: Full authentication support with JWT tokens
- **Schema Documentation**: Automatic documentation of DTOs and domain objects

### Authentication in Swagger UI

#### Method 1: OAuth2 Form (Recommended)

1. **Click "Authorize"**: Click the "Authorize" button in the top-right corner of Swagger UI
2. **Enter Credentials**: Use the built-in form to enter your username and password
3. **Select Scopes**: Choose the required scopes (read/write access)
4. **Get Token**: Click "Authorize" to automatically get a JWT token
5. **Use Protected Endpoints**: All `/api/**` endpoints will now work with authentication

#### Method 2: Manual Token

1. **Authenticate**: Use the `/authenticate` endpoint to get a JWT token
2. **Authorize**: Click the "Authorize" button and enter your token as `Bearer <your-token>`
3. **Use Protected Endpoints**: All `/api/**` endpoints require authentication

### Adding Documentation to New Controllers

#### Public Endpoints (No Authentication Required)

```java
@RestController
@RequestMapping("/public/your-endpoint")
@Tag(name = "Public API", description = "Public endpoints that don't require authentication")
public class PublicController {

    @GetMapping("/{id}")
    @Operation(summary = "Get by ID", description = "Retrieves an item by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = YourResponseDto.class))),
        @ApiResponse(responseCode = "404", description = "Not found")
    })
    public YourResponseDto findById(@Parameter(description = "ID of the item") 
                                   @PathVariable Integer id) {
        // Your implementation
    }
}
```

#### Protected Endpoints (Authentication Required)

```java
@RestController
@RequestMapping("/api/your-endpoint")
@Tag(name = "Your Tag", description = "Description of your API group")
@SecurityRequirement(name = "Bearer Authentication")
public class ProtectedController {

    @GetMapping("/{id}")
    @Operation(summary = "Get by ID", description = "Retrieves an item by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = YourResponseDto.class))),
        @ApiResponse(responseCode = "404", description = "Not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public YourResponseDto findById(@Parameter(description = "ID of the item") 
                                   @PathVariable Integer id) {
        // Your implementation
    }
}
```

## 🗃️ Flyway Database Migration Setup

This project uses Flyway for database migrations with PostgreSQL support, including repeatable migrations for development and testing environments.

### Configuration

#### Dependencies
- `flyway-core`: Core Flyway functionality
- `flyway-database-postgresql`: PostgreSQL-specific support

#### Properties Configuration
```properties
# Flyway Configuration
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
spring.flyway.baseline-on-migrate=true
spring.flyway.validate-on-migrate=true

# Disable default schema initialization since we're using Flyway
spring.sql.init.mode=never
```

### Migration Files

Migrations are located in `src/main/resources/db/migration/` and follow the naming convention:
- `V{version}__{description}.sql` for versioned migrations
- `R__{description}.sql` for repeatable migrations

#### File Structure
```
src/main/resources/db/migration/
├── V1__Create_initial_schema.sql          # Versioned migration (runs once)
├── dev/
│   └── R__Insert_dev_data.sql             # Repeatable migration (runs every time)
└── test/
    └── R__Insert_test_data.sql             # Repeatable migration (runs every time)
```

### Migration Types

1. **Versioned Migrations** (`V1__`, `V2__`, etc.)
   - Run only once
   - Tracked in Flyway's schema history
   - Used for schema changes

2. **Repeatable Migrations** (`R__`)
   - Run every time the application starts
   - Not tracked in schema history
   - Perfect for data seeding in development and testing

### Current Migrations

1. **V1__Create_initial_schema.sql**: Creates the initial database schema
   - **Location**: `src/main/resources/db/migration/V1__Create_initial_schema.sql`
   - **Purpose**: Creates all tables including Users, Post, tag, post_tag, and user_tag
   - **Includes**: Password field for JWT authentication

2. **R__Insert_dev_data.sql**: Repeatable migration for development
   - **Location**: `src/main/resources/db/migration/dev/R__Insert_dev_data.sql`
   - **Behavior**: Executes every time the application starts with the `dev` profile
   - **Data**: Inserts users with encrypted passwords and posts

3. **R__Insert_test_data.sql**: Repeatable migration for testing
   - **Location**: `src/main/resources/db/migration/test/R__Insert_test_data.sql`
   - **Behavior**: Executes every time the application starts with the `test` profile
   - **Data**: Inserts test data optimized for testing scenarios

### Usage

#### Running Migrations
Migrations run automatically when the application starts. You can also run them manually:

```bash
# Using Maven
mvn flyway:migrate

# Using Flyway CLI (if installed)
flyway migrate
```

#### Running with Dev Profile
```bash
# Set the dev profile
export DEFAULT_PROFILE=dev

# Or run with explicit profile
java -jar your-app.jar --spring.profiles.active=dev

# Or using Maven
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

#### Running with Test Profile
```bash
# Set the test profile
export DEFAULT_PROFILE=test

# Or run with explicit profile
java -jar your-app.jar --spring.profiles.active=test

# Or using Maven
mvn spring-boot:run -Dspring-boot.run.profiles=test
```

### Repeatable Migration Behavior

#### How It Works

- **Dev Profile**: Migration executes every startup
- **Test Profile**: Migration executes every startup
- **Other Profiles**: Migrations are ignored
- **Data Freshness**: Always starts with clean data
- **No Conflicts**: No duplicate key issues

#### Key Features
✅ **Always fresh data**: Clears existing data and re-inserts  
✅ **Profile-specific**: Only runs when `dev` or `test` profile is active  
✅ **Simple SQL**: No Java code needed  
✅ **Repeatable**: Runs every application startup  
✅ **Same data structure**: Exact replica of your JSON data  

### Useful Commands

```bash
# Check migration status
mvn flyway:info

# Validate migrations
mvn flyway:validate

# Clean database (DANGER: removes all data)
mvn flyway:clean

# Repair migration history
mvn flyway:repair
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

The project uses environment variables for configuration. The application automatically loads environment variables from your system or IDE configuration.

**Note**: The project uses a `DEFAULT_PROFILE` environment variable to determine the active Spring profile. Set this to `dev` for development or `test` for testing.

**Example environment variables for your IDE or system**:
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

**Note**: The application uses Spring Boot's built-in environment variable support. Configure these variables in your IDE or system environment as needed.

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

### Docker Compose Configuration

The project includes a `compose.yaml` file for local PostgreSQL database setup:

```yaml
services:
  postgres:
    container_name: blog_postgres_db
    image: 'postgres:16.0'
    environment:
      - 'POSTGRES_DB=blog'
      - 'POSTGRES_PASSWORD=secret_password'
      - 'POSTGRES_USER=blog'
    ports:
      - '5432'
```

### Development

The application includes several development tools:

- **Spring Boot DevTools**: Automatic restart on code changes
- **Spring Shell**: Interactive CLI for testing features
- **Docker Compose**: Local PostgreSQL database
- **Flyway**: Automatic database migrations

## 🔧 Configuration

Key configuration files:
- `application.properties`: Main application configuration
- `compose.yaml`: Docker Compose for local development
- `pom.xml`: Maven dependencies and build configuration

## 📝 Additional Documentation

- `.cursorrules`: Development guidelines and coding standards 