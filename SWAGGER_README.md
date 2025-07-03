# Swagger/OpenAPI Documentation

This project has been configured with Swagger/OpenAPI documentation using SpringDoc OpenAPI.

## Accessing Swagger UI

Once you start the application, you can access the Swagger UI at:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs

## Features

- **Interactive API Documentation**: Browse and test all API endpoints directly from the browser
- **Request/Response Examples**: See example requests and responses for each endpoint
- **Authentication Support**: Ready for adding authentication documentation
- **Schema Documentation**: Automatic documentation of DTOs and domain objects

## Configuration

The Swagger configuration is located in:
- `src/main/java/es/jmjg/experiments/infrastructure/config/SwaggerConfig.java` - Main configuration
- `src/main/resources/application.properties` - Swagger UI settings

## Adding Documentation to New Controllers

To add Swagger documentation to new controllers, use these annotations:

```java
@RestController
@RequestMapping("/api/your-endpoint")
@Tag(name = "Your Tag", description = "Description of your API group")
public class YourController {

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

## Available Annotations

- `@Tag` - Group related endpoints
- `@Operation` - Describe the operation
- `@Parameter` - Document parameters
- `@ApiResponses` - Document possible responses
- `@Schema` - Document data models
- `@Content` - Specify response content

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

For production environments, consider:
- Disabling Swagger UI in production profiles
- Adding authentication to Swagger UI
- Restricting access to API documentation endpoints 