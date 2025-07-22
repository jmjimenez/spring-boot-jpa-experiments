package es.jmjg.experiments.domain.exception;

public class TagInUseException extends RuntimeException {

  public TagInUseException(String message) {
    super(message);
  }
}