package es.jmjg.experiments.domain.user.exception;

import java.util.UUID;

/** Exception thrown when a user is not found in the application layer. */
public class UserNotFound extends RuntimeException {

  public UserNotFound(String message) {
    super(message);
  }

  public UserNotFound(UUID uuid) {
    super("User not found with id: " + uuid);
  }
}
