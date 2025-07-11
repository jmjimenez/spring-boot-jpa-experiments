package es.jmjg.experiments.application.post.exception;

/**
 * Exception thrown when a post is not found in the application layer.
 */
public class PostNotFound extends RuntimeException {

    public PostNotFound(String message) {
        super(message);
    }

    public PostNotFound(Integer id) {
        super("Post not found with id: " + id);
    }
}
