package es.jmjg.experiments.domain.tag.exception;

import es.jmjg.experiments.domain.tag.entity.TagUuid;

/** Exception thrown when a tag is not found in the application layer. */
public class TagNotFound extends RuntimeException {

  public TagNotFound(String message) {
    super(message);
  }

  public TagNotFound(Integer id) {
    super("Tag not found with id: " + id);
  }

  public TagNotFound(TagUuid uuid) {
    super("Tag not found with id: " + uuid);
  }
}
