package es.jmjg.experiments.application.exception;

/**
 * Exception thrown when an invalid request is made in the application layer.
 */
public class InvalidRequest extends RuntimeException {

    public InvalidRequest(String message) {
        super(message);
    }

    public InvalidRequest(String message, Throwable cause) {
        super(message, cause);
    }
}
