# JWT Security Implementation

This application now includes JWT (JSON Web Token) authentication following the guidelines from [Bootify.io](https://bootify.io/spring-security/rest-api-spring-security-with-jwt.html).

## Overview

The JWT security implementation provides:
- Token-based authentication for REST API endpoints
- Stateless authentication (no server-side sessions)
- Secure password handling with BCrypt encryption
- 30-minute token validity

## Authentication Flow

1. **Get a Token**: Send credentials to `/authenticate` endpoint
2. **Use the Token**: Include the token in the `Authorization` header for protected endpoints
3. **Token Expiration**: Tokens expire after 30 minutes and need to be renewed

## API Endpoints

### Public Endpoints (No Authentication Required)
- `GET /` - Application root
- `POST /authenticate` - Authentication endpoint
- `GET /api-docs/**` - OpenAPI documentation
- `GET /swagger-ui/**` - Swagger UI

### Protected Endpoints (Authentication Required)
- `GET /api/**` - All API endpoints require authentication

## Usage Examples

### 1. Authenticate to Get a Token

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

### 2. Use the Token to Access Protected Endpoints

```bash
curl -X GET http://localhost:8080/api/users \
  -H "Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9..."
```

## Test Users

The application includes test users with the following credentials:

| Username | Password | Email |
|----------|----------|-------|
| `leanne_graham` | `password` | `leanne.graham@example.com` |
| `ervin_howell` | `password` | `ervin.howell@example.com` |
| `clementine_bauch` | `password` | `clementine.bauch@example.com` |
| `patricia_lebsack` | `password` | `patricia.lebsack@example.com` |
| `chelsey_dietrich` | `password` | `chelsey.dietrich@example.com` |

## Configuration

### JWT Secret
The JWT secret is configured in `application.properties`:
```properties
jwt.secret=your-512-bit-secret-key-here-make-sure-it-is-at-least-512-bits-long-for-hmac512-algorithm-to-work-properly-and-securely
```

**Important**: In production, replace this with a secure, randomly generated secret key.

### Token Validity
Tokens are valid for 30 minutes by default. This can be modified in `JwtTokenService.java`.

## Security Features

- **Password Encryption**: All passwords are encrypted using BCrypt
- **Token Signing**: JWT tokens are signed using HMAC512 algorithm
- **Stateless**: No server-side session storage
- **Role-based Access**: All authenticated users have `ROLE_USER` authority
- **CSRF Protection**: Disabled for REST API (not needed with JWT)
- **CORS**: Disabled (configure as needed for your frontend)

## Error Handling

- **401 Unauthorized**: Invalid credentials or missing/invalid token
- **403 Forbidden**: Valid token but insufficient permissions (not applicable with current setup)

## Testing

Run the integration tests to verify the JWT authentication:

```bash
mvn test -Dtest=AuthenticationControllerIntegrationTest
mvn test -Dtest=SecurityIntegrationTest
```

## Implementation Details

The JWT security implementation consists of:

1. **JwtTokenService**: Handles token generation and validation
2. **JwtUserDetailsService**: Loads user details from the database
3. **JwtRequestFilter**: Validates tokens in incoming requests
4. **JwtSecurityConfig**: Configures Spring Security
5. **AuthenticationController**: Provides the `/authenticate` endpoint

All components follow Spring Security best practices and the Bootify.io guidelines.
