package es.jmjg.experiments.application.post;

import es.jmjg.experiments.application.post.dto.SavePostCommentDto;
import es.jmjg.experiments.domain.post.entity.Post;
import es.jmjg.experiments.domain.post.entity.PostComment;
import es.jmjg.experiments.domain.post.exception.PostNotFound;
import es.jmjg.experiments.domain.post.repository.PostCommentRepository;
import es.jmjg.experiments.domain.post.repository.PostRepository;
import es.jmjg.experiments.domain.shared.exception.InvalidRequest;
import es.jmjg.experiments.domain.user.entity.User;
import es.jmjg.experiments.domain.user.exception.UserNotFound;
import es.jmjg.experiments.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SavePostComment {

  private final PostRepository postRepository;
  private final UserRepository userRepository;
  private final PostCommentRepository postCommentRepository;

  public SavePostComment(PostRepository postRepository, UserRepository userRepository, PostCommentRepository postCommentRepository) {
    this.postRepository = postRepository;
    this.userRepository = userRepository;
    this.postCommentRepository = postCommentRepository;
  }

  @Transactional
  public PostComment save(SavePostCommentDto savePostCommentDto) {
    if (savePostCommentDto.authenticatedUser() == null) {
      throw new InvalidRequest("Comment must have a user");
    }

    User user = userRepository.findByUuid(savePostCommentDto.authenticatedUser().id())
      .orElseThrow(() -> new UserNotFound("User not found"));

    Post post = postRepository.findByUuid(savePostCommentDto.postUuid())
      .orElseThrow(() -> new PostNotFound("Post not found"));

    if (savePostCommentDto.comment().isEmpty()) {
      throw new InvalidRequest("Comment cannot be empty");
    }

    PostComment postComment = new PostComment();
    postComment.setUuid(savePostCommentDto.uuid());
    postComment.setPost(post);
    postComment.setComment(savePostCommentDto.comment());
    postComment.setUser(user);

    post.getComments().add(postComment);

    try {
      return postCommentRepository.save(postComment);
    } catch (Exception e) {
      throw new InvalidRequest("Could not save Post Comment with error: " + e.getMessage());
    }
  }
}
