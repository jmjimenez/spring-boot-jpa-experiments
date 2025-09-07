package es.jmjg.experiments.domain.post.exception;

import java.util.UUID;

/** Exception thrown when a post is not found in the application layer. */
public class PostNotFound extends RuntimeException {

  public PostNotFound(String message) {
    super(message);
  }

  public PostNotFound(Integer id) {
    super("Post not found with id: " + id);
  }

  public PostNotFound(UUID uuid) {
    super("Post not found with uuid: " + uuid);
  }
}
