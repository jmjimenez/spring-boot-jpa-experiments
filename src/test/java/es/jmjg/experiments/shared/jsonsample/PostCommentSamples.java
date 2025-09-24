package es.jmjg.experiments.shared.jsonsample;

import es.jmjg.experiments.domain.post.entity.PostComment;
import java.time.format.DateTimeFormatter;

public final class PostCommentSamples {
  private PostCommentSamples() {
    // Utility class - prevent instantiation
  }


  public static String createAddPostCommentRequestJson(PostComment postComment) {
    return """
        {
            "id":"%s",
            "comment":"%s"
        }
        """.formatted(postComment.getUuid(), postComment.getComment());
  }

  public static String createAddPostCommentResponseJson(PostComment postComment) {
    return """
        {
            "id":"%s",
            "user-id":"%s",
            "post-id":"%s",
            "comment":"%s"
        }
        """.formatted(postComment.getUuid(), postComment.getUser().getUuid(), postComment.getPost().getUuid(), postComment.getComment());
  }

  public static String createFindPostCommentByUuidJsonResponse(PostComment postComment) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    return """
        {
            "id":"%s",
            "user-id":"%s",
            "post-id":"%s",
            "comment":"%s",
            "created-time":"%s"
        }
        """.formatted(postComment.getUuid(), postComment.getPost().getUuid(), postComment.getUser().getUuid(), postComment.getComment(), postComment.getCreatedAt().format(formatter));
  }
}
