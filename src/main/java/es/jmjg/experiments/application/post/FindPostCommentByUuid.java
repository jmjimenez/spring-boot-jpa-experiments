package es.jmjg.experiments.application.post;

import es.jmjg.experiments.domain.post.entity.PostComment;
import es.jmjg.experiments.domain.post.exception.PostCommentNotFound;
import es.jmjg.experiments.domain.post.repository.PostCommentRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FindPostCommentByUuid {

  private final PostCommentRepository postCommentRepository;

  public FindPostCommentByUuid(PostCommentRepository postCommentRepository) {
    this.postCommentRepository = postCommentRepository;
  }

  @Transactional(readOnly = true)
  public PostComment findByUuid(UUID postUuid, UUID commentUuid) {
    if (postUuid == null) {
      throw new IllegalArgumentException("post id cannot be null");
    }

    if (commentUuid == null) {
      throw new IllegalArgumentException("id cannot be null");
    }

    PostComment postComment = postCommentRepository.findByUuid(commentUuid).orElseThrow(() -> new PostCommentNotFound("Post comment with id " + commentUuid + " not found"));

    if (!postComment.getPost().getUuid().equals(postUuid)) {
      throw new PostCommentNotFound("Post comment with id " + commentUuid + " not found for post with id " + postUuid);
    }

    return postComment;
  }
}
