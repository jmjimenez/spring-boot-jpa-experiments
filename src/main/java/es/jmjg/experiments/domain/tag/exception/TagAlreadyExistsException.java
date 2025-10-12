package es.jmjg.experiments.domain.tag.exception;

import es.jmjg.experiments.domain.tag.entity.TagUuid;

/**
 * Exception thrown when attempting to create a tag that already exists.
 */
public class TagAlreadyExistsException extends RuntimeException {

  public TagAlreadyExistsException(String message) {
    super(message);
  }

  public TagAlreadyExistsException(String name, TagUuid uuid) {
    super("Tag with name '" + name + "' already exists with id '" + uuid + "'");
  }

  public TagAlreadyExistsException(TagUuid uuid) {
    super("Tag with id '" + uuid + "' already exists");
  }
}
