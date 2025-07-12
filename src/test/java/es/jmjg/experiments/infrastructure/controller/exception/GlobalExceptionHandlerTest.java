package es.jmjg.experiments.infrastructure.controller.exception;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

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
  void handlePostNotFoundException_ShouldReturn404() {
    // Given
    PostNotFoundException exception = new PostNotFoundException("Post with id 1 not found");

    // When
    ResponseEntity<ApiErrorResponse> response =
        exceptionHandler.handlePostNotFoundException(exception, webRequest);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(response.getBody())
        .isNotNull()
        .satisfies(
            body -> {
              assertThat(body.getStatus()).isEqualTo(404);
              assertThat(body.getError()).isEqualTo("Not Found");
              assertThat(body.getMessage()).isEqualTo("Post with id 1 not found");
              assertThat(body.getPath()).isEqualTo("uri=/api/posts/1");
            });
  }

  @Test
  void handleAllExceptions_ShouldReturn500() {
    // Given
    RuntimeException exception = new RuntimeException("Database connection failed");

    // When
    ResponseEntity<ApiErrorResponse> response =
        exceptionHandler.handleAllExceptions(exception, webRequest);

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
  void handlePostNotFoundException_WithDefaultMessage_ShouldReturn404() {
    // Given
    PostNotFoundException exception = new PostNotFoundException();

    // When
    ResponseEntity<ApiErrorResponse> response =
        exceptionHandler.handlePostNotFoundException(exception, webRequest);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(response.getBody())
        .isNotNull()
        .satisfies(
            body -> {
              assertThat(body.getStatus()).isEqualTo(404);
              assertThat(body.getError()).isEqualTo("Not Found");
              assertThat(body.getMessage()).isEqualTo("Post not found");
            });
  }
}
