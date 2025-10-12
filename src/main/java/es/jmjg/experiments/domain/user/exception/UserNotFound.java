package es.jmjg.experiments.domain.user.exception;

import es.jmjg.experiments.domain.user.entity.UserUuid;

/** Exception thrown when a user is not found in the application layer. */
public class UserNotFound extends RuntimeException {

  public UserNotFound(String message) {
    super(message);
  }

  public UserNotFound(UserUuid uuid) {
    super("User not found with id: " + uuid);
  }
}
