package es.jmjg.experiments.application.post.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import es.jmjg.experiments.application.post.FindPostCommentByUuid;
import es.jmjg.experiments.domain.post.entity.Post;
import es.jmjg.experiments.domain.post.entity.PostComment;
import es.jmjg.experiments.domain.post.exception.PostCommentNotFound;
import es.jmjg.experiments.domain.user.entity.User;
import es.jmjg.experiments.infrastructure.repository.PostRepositoryImpl;
import es.jmjg.experiments.infrastructure.repository.UserRepositoryImpl;
import es.jmjg.experiments.infrastructure.repository.jpa.PostCommentRepositoryImpl;
import es.jmjg.experiments.shared.BaseIntegration;
import es.jmjg.experiments.shared.PostFactory;
import es.jmjg.experiments.shared.TestDataSamples;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class FindPostCommentByUuidIntegrationTest extends BaseIntegration {

  @Autowired
  private FindPostCommentByUuid findPostCommentByUuid;

  @Autowired
  private PostRepositoryImpl postRepository;

  @Autowired
  private UserRepositoryImpl userRepository;

  @Autowired
  private PostCommentRepositoryImpl postCommentRepository;

  @Test
  void findByUuid_WhenPostCommentExists_ShouldReturnPostComment() {
    // Given
    String commentText = "This is a test comment";
    User testUser = userRepository.findByUuid(TestDataSamples.LEANNE_UUID).orElseThrow(() -> new RuntimeException("User sample not found"));
    Post testPost = postRepository.findByUuid(TestDataSamples.POST_1_UUID).orElseThrow(() -> new RuntimeException("Post sample not found"));
    PostComment testPostComment = PostFactory.createPostComment(testUser, testPost, commentText);
    postCommentRepository.save(testPostComment);

    // When
    PostComment result = findPostCommentByUuid.findByUuid(testPost.getUuid(), testPostComment.getUuid());

    // Then
    assertThat(result.getUuid()).isEqualTo(testPostComment.getUuid());
    assertThat(result.getUser().getId()).isEqualTo(testUser.getId());
    assertThat(result.getPost().getId()).isEqualTo(testPost.getId());
    assertThat(result.getComment()).isEqualTo(commentText);
  }

  @Test
  void findByUuid_WhenPostCommentDoesNotExist_ShouldThrowPostCommentNotFoundException() {
    // Given
    UUID nonExistentUuid = UUID.randomUUID();
    Post testPost = postRepository.findByUuid(TestDataSamples.POST_1_UUID).orElseThrow(() -> new RuntimeException("Post sample not found"));

    // When
    assertThatThrownBy(() -> findPostCommentByUuid.findByUuid(testPost.getUuid(), nonExistentUuid))
        .isInstanceOf(PostCommentNotFound.class)
        .hasMessage("Post comment with id " + nonExistentUuid + " not found");
  }

  @Test
  void findByUuid_WhenUuidIsNull_ShouldThrowIllegalArgumentException() {
    // Given
    Post testPost = postRepository.findByUuid(TestDataSamples.POST_1_UUID).orElseThrow(() -> new RuntimeException("Post sample not found"));

    // When
    assertThatThrownBy(() -> findPostCommentByUuid.findByUuid(testPost.getUuid(), null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("id cannot be null");
  }
}
