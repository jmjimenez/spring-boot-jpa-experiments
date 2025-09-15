package es.jmjg.experiments.domain.post.exception;

public class PostCommentNotFound extends RuntimeException {

  public PostCommentNotFound(String message) {
    super(message);
  }
}
