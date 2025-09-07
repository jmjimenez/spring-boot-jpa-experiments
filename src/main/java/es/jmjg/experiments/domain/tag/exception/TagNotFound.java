package es.jmjg.experiments.domain.tag.exception;

import java.util.UUID;

/** Exception thrown when a tag is not found in the application layer. */
public class TagNotFound extends RuntimeException {

  public TagNotFound(String message) {
    super(message);
  }

  public TagNotFound(Integer id) {
    super("Tag not found with id: " + id);
  }

  public TagNotFound(UUID uuid) {
    super("Tag not found with uuid: " + uuid);
  }
}
