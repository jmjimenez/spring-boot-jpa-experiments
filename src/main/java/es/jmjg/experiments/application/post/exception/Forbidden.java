package es.jmjg.experiments.application.post.exception;

/** Exception thrown when access is forbidden in the application layer. */
public class Forbidden extends RuntimeException {

  public Forbidden(String message) {
    super(message);
  }

  public Forbidden(String message, Throwable cause) {
    super(message, cause);
  }
}
