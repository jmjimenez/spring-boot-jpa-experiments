package es.jmjg.experiments.application.tag.exception;

import java.util.UUID;

/**
 * Exception thrown when attempting to delete a tag that is currently in use.
 */
public class TagInUseException extends RuntimeException {

  public TagInUseException(String message) {
    super(message);
  }

  public TagInUseException(UUID uuid) {
    super("Cannot delete tag with uuid: " + uuid + " because it is currently in use");
  }

  public TagInUseException(String name, UUID uuid) {
    super("Cannot delete tag '" + name + "' with uuid: " + uuid + " because it is currently in use");
  }
}