package es.jmjg.experiments.infrastructure.controller.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import es.jmjg.experiments.application.post.exception.InvalidRequest;
import es.jmjg.experiments.application.post.exception.PostNotFound;
import es.jmjg.experiments.application.user.exception.UserNotFound;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(PostNotFoundException.class)
  public ResponseEntity<ApiErrorResponse> handlePostNotFoundException(PostNotFoundException ex,
      WebRequest request) {

    log.warn("Post not found: {}", ex.getMessage());

    ApiErrorResponse errorResponse = ApiErrorResponse.builder().timestamp(LocalDateTime.now())
        .status(HttpStatus.NOT_FOUND.value()).error(HttpStatus.NOT_FOUND.getReasonPhrase())
        .message(ex.getMessage()).path(request.getDescription(false)).build();

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }

  @ExceptionHandler(PostNotFound.class)
  public ResponseEntity<ApiErrorResponse> handlePostNotFound(PostNotFound ex, WebRequest request) {

    log.warn("Post not found: {}", ex.getMessage());

    ApiErrorResponse errorResponse = ApiErrorResponse.builder().timestamp(LocalDateTime.now())
        .status(HttpStatus.NOT_FOUND.value()).error(HttpStatus.NOT_FOUND.getReasonPhrase())
        .message(ex.getMessage()).path(request.getDescription(false)).build();

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }

  @ExceptionHandler(UserNotFound.class)
  public ResponseEntity<ApiErrorResponse> handleUserNotFound(UserNotFound ex, WebRequest request) {

    log.warn("User not found: {}", ex.getMessage());

    ApiErrorResponse errorResponse = ApiErrorResponse.builder().timestamp(LocalDateTime.now())
        .status(HttpStatus.NOT_FOUND.value()).error(HttpStatus.NOT_FOUND.getReasonPhrase())
        .message(ex.getMessage()).path(request.getDescription(false)).build();

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }

  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<ApiErrorResponse> handleUserNotFoundException(UserNotFoundException ex,
      WebRequest request) {

    log.warn("User not found: {}", ex.getMessage());

    ApiErrorResponse errorResponse = ApiErrorResponse.builder().timestamp(LocalDateTime.now())
        .status(HttpStatus.NOT_FOUND.value()).error(HttpStatus.NOT_FOUND.getReasonPhrase())
        .message(ex.getMessage()).path(request.getDescription(false)).build();

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }

  @ExceptionHandler(InvalidRequest.class)
  public ResponseEntity<ApiErrorResponse> handleInvalidRequest(InvalidRequest ex,
      WebRequest request) {

    log.warn("Invalid request: {}", ex.getMessage());

    ApiErrorResponse errorResponse = ApiErrorResponse.builder().timestamp(LocalDateTime.now())
        .status(HttpStatus.BAD_REQUEST.value()).error(HttpStatus.BAD_REQUEST.getReasonPhrase())
        .message(ex.getMessage()).path(request.getDescription(false)).build();

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiErrorResponse> handleValidationExceptions(
      MethodArgumentNotValidException ex, WebRequest request) {

    log.warn("Validation error: {}", ex.getMessage());

    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getAllErrors().forEach((error) -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });

    ApiErrorResponse errorResponse = ApiErrorResponse.builder().timestamp(LocalDateTime.now())
        .status(HttpStatus.BAD_REQUEST.value()).error(HttpStatus.BAD_REQUEST.getReasonPhrase())
        .message("Validation failed").path(request.getDescription(false)).details(errors).build();

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ApiErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex,
      WebRequest request) {
    log.warn("Type mismatch: {}", ex.getMessage());
    ApiErrorResponse errorResponse = ApiErrorResponse.builder().timestamp(LocalDateTime.now())
        .status(HttpStatus.BAD_REQUEST.value()).error(HttpStatus.BAD_REQUEST.getReasonPhrase())
        .message("Invalid parameter: " + ex.getName() + ". " + ex.getMessage())
        .path(request.getDescription(false)).build();
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiErrorResponse> handleAllExceptions(Exception ex, WebRequest request) {

    log.error("Unhandled exception occurred: {}", ex.getMessage(), ex);

    ApiErrorResponse errorResponse = ApiErrorResponse.builder().timestamp(LocalDateTime.now())
        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
        .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
        .message("An unexpected error occurred").path(request.getDescription(false)).build();

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
  }
}
