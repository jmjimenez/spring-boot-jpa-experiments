package es.jmjg.experiments.infrastructure.controller.exception.integration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import es.jmjg.experiments.infrastructure.config.exception.ApiErrorResponse;
import es.jmjg.experiments.shared.BaseControllerIntegration;
import es.jmjg.experiments.shared.TestDataSamples;

class GlobalExceptionHandlerIntegrationTest extends BaseControllerIntegration {

  @Test
  void shouldReturnBadRequestForInvalidUrlPattern() {
    // Test that /api/posts/ (missing UUID) returns 400 Bad Request, not 500
    // Internal Server Error
    HttpEntity<String> request = createAuthenticatedRequest(TestDataSamples.ADMIN_USERNAME,
        TestDataSamples.ADMIN_PASSWORD);

    ResponseEntity<ApiErrorResponse> response = restTemplate.exchange(
        "/api/posts/",
        HttpMethod.GET,
        request,
        ApiErrorResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

    ApiErrorResponse responseBody = response.getBody();
    assertThat(responseBody).isNotNull().satisfies(errorResponse -> {
      assertThat(errorResponse.getStatus()).isEqualTo(400);
      assertThat(errorResponse.getError()).isEqualTo("Bad Request");
      assertThat(errorResponse.getMessage()).contains("Resource not found");
      assertThat(errorResponse.getPath()).isEqualTo("uri=/api/posts/");
    });
  }

  @Test
  void shouldReturnBadRequestForMalformedUuid() {
    // Test that malformed UUID returns 400 Bad Request, not 500 Internal Server
    // Error
    HttpEntity<String> request = createAuthenticatedRequest(TestDataSamples.ADMIN_USERNAME,
        TestDataSamples.ADMIN_PASSWORD);

    ResponseEntity<ApiErrorResponse> response = restTemplate.exchange(
        "/api/posts/invalid-uuid-format",
        HttpMethod.GET,
        request,
        ApiErrorResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

    ApiErrorResponse responseBody = response.getBody();
    assertThat(responseBody).isNotNull().satisfies(errorResponse -> {
    assertThat(errorResponse.getStatus()).isEqualTo(400);
      assertThat(errorResponse.getError()).isEqualTo("Bad Request");
      assertThat(errorResponse.getMessage()).contains("Invalid parameter: uuid");
      assertThat(errorResponse.getPath()).isEqualTo("uri=/api/posts/invalid-uuid-format");
    });
  }

  @Test
  void shouldReturnBadRequestForInvalidUrlWithTrailingSlash() {
    // Test that /api/posts/ (with trailing slash) returns 400 Bad Request, not 500
    // Internal Server Error
    HttpEntity<String> request = createAuthenticatedRequest(TestDataSamples.ADMIN_USERNAME,
        TestDataSamples.ADMIN_PASSWORD);

    ResponseEntity<ApiErrorResponse> response = restTemplate.exchange(
        "/api/posts/",
        HttpMethod.GET,
        request,
        ApiErrorResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

    ApiErrorResponse responseBody = response.getBody();
    assertThat(responseBody).isNotNull().satisfies(errorResponse -> {
    assertThat(errorResponse.getStatus()).isEqualTo(400);
    assertThat(errorResponse.getError()).isEqualTo("Bad Request");
    assertThat(errorResponse.getMessage()).contains("Resource not found");
      assertThat(errorResponse.getPath()).isEqualTo("uri=/api/posts/");
    });
  }

  @Test
  void shouldReturnBadRequestForInvalidUrlWithoutTrailingSlash() {
    // Test that /api/posts (without trailing slash) returns 200 OK because it
    // matches @GetMapping("")
    // This is not an error scenario, so we test that it doesn't return an error
    HttpEntity<String> request = createAuthenticatedRequest(TestDataSamples.ADMIN_USERNAME,
        TestDataSamples.ADMIN_PASSWORD);

    ResponseEntity<Object> response = restTemplate.exchange(
        "/api/posts",
        HttpMethod.GET,
        request,
        Object.class);

    // This should return 200 OK, not an error
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
  }
}
