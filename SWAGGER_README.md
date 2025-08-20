# Swagger/OpenAPI Documentation

This project has been configured with Swagger/OpenAPI documentation using SpringDoc OpenAPI.

## Accessing Swagger UI

Once you start the application, you can access the Swagger UI at:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs

## Features

- **Interactive API Documentation**: Browse and test all API endpoints directly from the browser
- **Request/Response Examples**: See example requests and responses for each endpoint
- **OAuth2 Password Flow**: Built-in authentication form for requesting JWT tokens
- **JWT Bearer Token Authentication**: Full authentication support with JWT tokens
- **Schema Documentation**: Automatic documentation of DTOs and domain objects

## Authentication

The API uses JWT Bearer token authentication with OAuth2 password flow. To use protected endpoints:

### Method 1: OAuth2 Form (Recommended)

1. **Click "Authorize"**: Click the "Authorize" button in the top-right corner of Swagger UI
2. **Enter Credentials**: Use the built-in form to enter your username and password
3. **Select Scopes**: Choose the required scopes (read/write access)
4. **Get Token**: Click "Authorize" to automatically get a JWT token
5. **Use Protected Endpoints**: All `/api/**` endpoints will now work with authentication

**Note**: The OAuth2 form sends credentials as form-encoded data to the `/authenticate` endpoint.

### Method 2: Manual Token

1. **Authenticate**: Use the `/authenticate` endpoint to get a JWT token
2. **Authorize**: Click the "Authorize" button and enter your token as `Bearer <your-token>`
3. **Use Protected Endpoints**: All `/api/**` endpoints require authentication

### Method 3: Direct JSON Authentication

You can also use the `/authenticate` endpoint directly with JSON:

```bash
curl -X POST http://localhost:8080/authenticate \
  -H "Content-Type: application/json" \
  -d '{"login": "leanne_graham", "password": "password"}'
```

### Troubleshooting

If you encounter issues with the OAuth2 form:

1. **Content-Type Error**: The endpoint supports both JSON and form-encoded data
2. **Parameter Names**: OAuth2 uses `username`/`password`, JSON uses `login`/`password`
3. **Scopes**: Select appropriate scopes (read/write) in the OAuth2 form
4. **Debug Logging**: Check application logs for detailed authentication information
5. **Manual Token**: If OAuth2 form doesn't work, use Method 2 or 3 above

### Test Users

You can use these test credentials for authentication:

| Username | Password |
|----------|----------|
| `leanne_graham` | `password` |
| `ervin_howell` | `password` |
| `clementine_bauch` | `password` |
| `patricia_lebsack` | `password` |
| `chelsey_dietrich` | `password` |
| `admin` | `testpass` |

### Example Authentication Flow

1. **Get Token**:
   ```bash
   curl -X POST http://localhost:8080/authenticate \
     -H "Content-Type: application/json" \
     -d '{"login": "leanne_graham", "password": "password"}'
   ```

2. **Use Token**:
   ```bash
   curl -X GET http://localhost:8080/api/users \
     -H "Authorization: Bearer <your-jwt-token>"
   ```

## Configuration

The Swagger configuration is located in:
- `src/main/java/es/jmjg/experiments/infrastructure/config/SwaggerConfig.java` - Main configuration
- `src/main/resources/application.properties` - Swagger UI settings

## Adding Documentation to New Controllers

To add Swagger documentation to new controllers, use these annotations:

### Public Endpoints (No Authentication Required)

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

### Protected Endpoints (Authentication Required)

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

### Authentication Endpoint

```java
@PostMapping("/authenticate")
@Operation(
    summary = "Authenticate user", 
    description = "Authenticates a user and returns a JWT token",
    security = {})  // No security required for this endpoint
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Authentication successful", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = AuthenticationResponse.class))),
    @ApiResponse(responseCode = "401", description = "Invalid credentials")
})
public AuthenticationResponse authenticate(
    @Parameter(description = "User credentials", required = true) 
    @RequestBody @Valid AuthenticationRequest request) {
    // Your implementation
}
```

## Available Annotations

- `@Tag` - Group related endpoints
- `@Operation` - Describe the operation
- `@Parameter` - Document parameters
- `@ApiResponses` - Document possible responses
- `@Schema` - Document data models
- `@Content` - Specify response content
- `@SecurityRequirement` - Specify authentication requirements

## Customization

You can customize the Swagger UI appearance and behavior by modifying the properties in `application.properties`:

```properties
# Swagger UI path
springdoc.swagger-ui.path=/swagger-ui.html

# API docs path
springdoc.api-docs.path=/api-docs

# UI customization
springdoc.swagger-ui.operationsSorter=method
springdoc.swagger-ui.tagsSorter=alpha
springdoc.swagger-ui.doc-expansion=none
```

## Security

The application includes JWT Bearer token authentication with OAuth2 password flow:

- **OAuth2 Password Flow**: Built-in form for requesting tokens
- **Global Security**: All `/api/**` endpoints require authentication
- **Public Endpoints**: `/authenticate`, `/api-docs/**`, `/swagger-ui/**` are publicly accessible
- **Token Expiration**: JWT tokens expire after 30 minutes
- **Production Considerations**:
  - Disable Swagger UI in production profiles
  - Restrict access to API documentation endpoints
  - Use HTTPS in production environments