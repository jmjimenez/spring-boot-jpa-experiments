package es.jmjg.experiments.domain.tag.exception;

//TODO: move all application exceptions to domain
public class TagInUseException extends RuntimeException {

  public TagInUseException(String message) {
    super(message);
  }
}
