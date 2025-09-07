package es.jmjg.experiments.domain.shared.exception;

public class InvalidRequest extends RuntimeException {

  public InvalidRequest(String message) {
    super(message);
  }
}
