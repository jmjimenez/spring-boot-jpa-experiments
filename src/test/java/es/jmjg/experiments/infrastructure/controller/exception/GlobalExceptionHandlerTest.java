package es.jmjg.experiments.infrastructure.controller.exception;

import static org.assertj.core.api.Assertions.*;

import es.jmjg.experiments.infrastructure.config.exception.ApiErrorResponse;
import es.jmjg.experiments.infrastructure.config.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

import es.jmjg.experiments.domain.shared.exception.Forbidden;

class GlobalExceptionHandlerTest {

  private GlobalExceptionHandler exceptionHandler;
  private WebRequest webRequest;

  @BeforeEach
  void setUp() {
    exceptionHandler = new GlobalExceptionHandler();
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setRequestURI("/api/posts/1");
    webRequest = new ServletWebRequest(request);
  }

  @Test
  void handleAllExceptions_ShouldReturn500() {
    // Given
    RuntimeException exception = new RuntimeException("Database connection failed");

    // When
    ResponseEntity<ApiErrorResponse> response = exceptionHandler.handleAllExceptions(exception, webRequest);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    assertThat(response.getBody())
        .isNotNull()
        .satisfies(
            body -> {
              assertThat(body.getStatus()).isEqualTo(500);
              assertThat(body.getError()).isEqualTo("Internal Server Error");
              assertThat(body.getMessage()).isEqualTo("An unexpected error occurred");
              assertThat(body.getPath()).isEqualTo("uri=/api/posts/1");
            });
  }

  @Test
  void handleResponseStatusException_ShouldReturnCorrectStatus() {
    // Given
    ResponseStatusException exception = new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");

    // When
    ResponseEntity<ApiErrorResponse> response = exceptionHandler.handleResponseStatusException(exception, webRequest);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    assertThat(response.getBody())
        .isNotNull()
        .satisfies(
            body -> {
              assertThat(body.getStatus()).isEqualTo(401);
              assertThat(body.getError()).isEqualTo("Unauthorized");
              assertThat(body.getMessage()).isEqualTo("Invalid credentials");
              assertThat(body.getPath()).isEqualTo("uri=/api/posts/1");
            });
  }

  @Test
  void handleResponseStatusException_WithoutReason_ShouldUseMessage() {
    // Given
    ResponseStatusException exception = new ResponseStatusException(HttpStatus.FORBIDDEN);

    // When
    ResponseEntity<ApiErrorResponse> response = exceptionHandler.handleResponseStatusException(exception, webRequest);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    assertThat(response.getBody())
        .isNotNull()
        .satisfies(
            body -> {
              assertThat(body.getStatus()).isEqualTo(403);
              assertThat(body.getError()).isEqualTo("Forbidden");
              assertThat(body.getMessage()).isNotEmpty();
            });
  }

  @Test
  void handleForbidden_ShouldReturn403() {
    // Given
    Forbidden exception = new Forbidden("User is not authorized to update this post");

    // When
    ResponseEntity<ApiErrorResponse> response = exceptionHandler.handleForbidden(exception, webRequest);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    assertThat(response.getBody())
        .isNotNull()
        .satisfies(
            body -> {
              assertThat(body.getStatus()).isEqualTo(403);
              assertThat(body.getError()).isEqualTo("Forbidden");
              assertThat(body.getMessage()).isEqualTo("User is not authorized to update this post");
              assertThat(body.getPath()).isEqualTo("uri=/api/posts/1");
            });
  }
}
