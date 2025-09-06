package es.jmjg.experiments.application.tag.exception;

//TODO: move all application exceptions to domain
public class TagInUseException extends RuntimeException {

  public TagInUseException(String message) {
    super(message);
  }
}
