package es.jmjg.experiments.application.tag.exception;

import java.util.UUID;

/**
 * Exception thrown when attempting to create a tag that already exists.
 */
public class TagAlreadyExistsException extends RuntimeException {

  public TagAlreadyExistsException(String message) {
    super(message);
  }

  public TagAlreadyExistsException(String name, UUID uuid) {
    super("Tag with name '" + name + "' already exists");
  }

  public TagAlreadyExistsException(UUID uuid) {
    super("Tag with uuid '" + uuid + "' already exists");
  }
}