package es.jmjg.experiments.application.user.exception;

/**
 * Exception thrown when a user is not found in the application layer.
 */
public class UserNotFound extends RuntimeException {

    public UserNotFound(String message) {
        super(message);
    }

    public UserNotFound(Integer id) {
        super("User not found with id: " + id);
    }
}
