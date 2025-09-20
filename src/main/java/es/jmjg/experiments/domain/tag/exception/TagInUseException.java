package es.jmjg.experiments.domain.tag.exception;

public class TagInUseException extends RuntimeException {

  public TagInUseException(String message) {
    super(message);
  }
}
