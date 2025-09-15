package es.jmjg.experiments.infrastructure.config.exception;

import es.jmjg.experiments.domain.post.exception.PostCommentNotFound;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import es.jmjg.experiments.domain.post.exception.PostNotFound;
import es.jmjg.experiments.domain.shared.exception.Forbidden;
import es.jmjg.experiments.domain.shared.exception.InvalidRequest;
import es.jmjg.experiments.domain.tag.exception.TagAlreadyExistsException;
import es.jmjg.experiments.domain.tag.exception.TagInUseException;
import es.jmjg.experiments.domain.tag.exception.TagNotFound;
import es.jmjg.experiments.domain.user.exception.UserNotFound;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(PostNotFound.class)
  public ResponseEntity<ApiErrorResponse> handlePostNotFound(PostNotFound ex, WebRequest request) {

    log.warn("Post not found: {}", ex.getMessage());

    //TODO: refactor to use a common method to build the response
    ApiErrorResponse errorResponse = ApiErrorResponse.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.NOT_FOUND.value())
        .error(HttpStatus.NOT_FOUND.getReasonPhrase())
        .message(ex.getMessage())
        .path(request.getDescription(false))
        .build();

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }

  @ExceptionHandler(PostCommentNotFound.class)
  public ResponseEntity<ApiErrorResponse> handlePostCommentNotFound(PostCommentNotFound ex, WebRequest request) {

    log.warn("Post comment not found: {}", ex.getMessage());

    ApiErrorResponse errorResponse = ApiErrorResponse.builder()
      .timestamp(LocalDateTime.now())
      .status(HttpStatus.NOT_FOUND.value())
      .error(HttpStatus.NOT_FOUND.getReasonPhrase())
      .message(ex.getMessage())
      .path(request.getDescription(false))
      .build();

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }

  @ExceptionHandler(UserNotFound.class)
  public ResponseEntity<ApiErrorResponse> handleUserNotFound(UserNotFound ex, WebRequest request) {

    log.warn("User not found: {}", ex.getMessage());

    ApiErrorResponse errorResponse = ApiErrorResponse.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.NOT_FOUND.value())
        .error(HttpStatus.NOT_FOUND.getReasonPhrase())
        .message(ex.getMessage())
        .path(request.getDescription(false))
        .build();

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }

  @ExceptionHandler(TagNotFound.class)
  public ResponseEntity<ApiErrorResponse> handleTagNotFound(TagNotFound ex, WebRequest request) {

    log.warn("Tag not found: {}", ex.getMessage());

    ApiErrorResponse errorResponse = ApiErrorResponse.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.NOT_FOUND.value())
        .error(HttpStatus.NOT_FOUND.getReasonPhrase())
        .message(ex.getMessage())
        .path(request.getDescription(false))
        .build();

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }

  @ExceptionHandler(TagInUseException.class)
  public ResponseEntity<ApiErrorResponse> handleTagInUseException(
      TagInUseException ex, WebRequest request) {

    log.warn("Tag in use: {}", ex.getMessage());

    ApiErrorResponse errorResponse = ApiErrorResponse.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.CONFLICT.value())
        .error(HttpStatus.CONFLICT.getReasonPhrase())
        .message(ex.getMessage())
        .path(request.getDescription(false))
        .build();

    return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
  }

  @ExceptionHandler(TagAlreadyExistsException.class)
  public ResponseEntity<ApiErrorResponse> handleTagAlreadyExistsException(
      TagAlreadyExistsException ex, WebRequest request) {

    log.warn("Tag already exists: {}", ex.getMessage());

    ApiErrorResponse errorResponse = ApiErrorResponse.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.CONFLICT.value())
        .error(HttpStatus.CONFLICT.getReasonPhrase())
        .message(ex.getMessage())
        .path(request.getDescription(false))
        .build();

    return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
  }

  @ExceptionHandler(InvalidRequest.class)
  public ResponseEntity<ApiErrorResponse> handleInvalidRequest(
      InvalidRequest ex, WebRequest request) {

    log.warn("Invalid request: {}", ex.getMessage());

    ApiErrorResponse errorResponse = ApiErrorResponse.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.BAD_REQUEST.value())
        .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
        .message(ex.getMessage())
        .path(request.getDescription(false))
        .build();

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  @ExceptionHandler(Forbidden.class)
  public ResponseEntity<ApiErrorResponse> handleForbidden(
      Forbidden ex, WebRequest request) {

    log.warn("Access forbidden: {}", ex.getMessage());

    ApiErrorResponse errorResponse = ApiErrorResponse.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.FORBIDDEN.value())
        .error(HttpStatus.FORBIDDEN.getReasonPhrase())
        .message(ex.getMessage())
        .path(request.getDescription(false))
        .build();

    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiErrorResponse> handleValidationExceptions(
      MethodArgumentNotValidException ex, WebRequest request) {

    log.warn("Validation error: {}", ex.getMessage());

    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult()
        .getAllErrors()
        .forEach(
            (error) -> {
              String fieldName = ((FieldError) error).getField();
              String errorMessage = error.getDefaultMessage();
              errors.put(fieldName, errorMessage);
            });

    ApiErrorResponse errorResponse = ApiErrorResponse.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.BAD_REQUEST.value())
        .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
        .message("Validation failed")
        .path(request.getDescription(false))
        .details(errors)
        .build();

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ApiErrorResponse> handleTypeMismatch(
      MethodArgumentTypeMismatchException ex, WebRequest request) {
    log.warn("Type mismatch: {}", ex.getMessage());
    ApiErrorResponse errorResponse = ApiErrorResponse.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.BAD_REQUEST.value())
        .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
        .message("Invalid parameter: " + ex.getName() + ". " + ex.getMessage())
        .path(request.getDescription(false))
        .build();
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<ApiErrorResponse> handleResponseStatusException(
      ResponseStatusException ex, WebRequest request) {
    log.warn("Response status exception: {}", ex.getMessage());

    HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
    ApiErrorResponse errorResponse = ApiErrorResponse.builder()
        .timestamp(LocalDateTime.now())
        .status(status.value())
        .error(status.getReasonPhrase())
        .message(ex.getReason() != null ? ex.getReason() : ex.getMessage())
        .path(request.getDescription(false))
        .build();

    return ResponseEntity.status(status).body(errorResponse);
  }

  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<ApiErrorResponse> handleNoResourceFoundException(
      NoResourceFoundException ex, WebRequest request) {
    log.warn("No resource found: {}", ex.getMessage());
    ApiErrorResponse errorResponse = ApiErrorResponse.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.BAD_REQUEST.value())
        .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
        .message("Resource not found: " + ex.getMessage())
        .path(request.getDescription(false))
        .build();
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ApiErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, WebRequest request) {

    log.error("HttpMessageNotReadable exception occurred: {}", ex.getMessage(), ex);

    ApiErrorResponse errorResponse = ApiErrorResponse.builder()
      .timestamp(LocalDateTime.now())
      .status(HttpStatus.BAD_REQUEST.value())
      .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
      .message("Malformed or missing request body")
      .path(request.getDescription(false))
      .build();
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiErrorResponse> handleAllExceptions(Exception ex, WebRequest request) {

    log.error("Unhandled exception occurred: {}", ex.getMessage(), ex);

    ApiErrorResponse errorResponse = ApiErrorResponse.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
        .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
        .message("An unexpected error occurred")
        .path(request.getDescription(false))
        .build();

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
  }
}
