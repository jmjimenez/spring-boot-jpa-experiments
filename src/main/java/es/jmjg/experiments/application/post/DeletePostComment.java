package es.jmjg.experiments.application.post;

import es.jmjg.experiments.application.post.dto.DeletePostCommentDto;
import es.jmjg.experiments.domain.post.exception.PostCommentNotFound;
import es.jmjg.experiments.domain.post.repository.PostCommentRepository;
import es.jmjg.experiments.domain.shared.exception.Forbidden;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeletePostComment {

  private final PostCommentRepository postCommentRepository;

  public DeletePostComment(PostCommentRepository postCommentRepository) {
    this.postCommentRepository = postCommentRepository;
  }

  @Transactional
  public void delete(DeletePostCommentDto deletePostCommentDto) {
    if (!deletePostCommentDto.authenticatedUser().isAdmin()) {
      throw new Forbidden("You are not allowed to delete comments");
    }

    var postComment = postCommentRepository.findByUuid(deletePostCommentDto.uuid()).orElseThrow(() -> new PostCommentNotFound("Post comment with id " + deletePostCommentDto.uuid() + " not found"));

    if (!postComment.getPost().getUuid().equals(deletePostCommentDto.postUuid())) {
      throw new PostCommentNotFound("Post comment with id " + deletePostCommentDto.uuid() + " not found for post with id " + deletePostCommentDto.postUuid());
    }

    postCommentRepository.deleteById(postComment.getId());
  }
}
