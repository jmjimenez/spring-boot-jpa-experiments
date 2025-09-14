package es.jmjg.experiments.application.post.integration;

import static org.assertj.core.api.Assertions.assertThat;

import es.jmjg.experiments.application.post.SavePostComment;
import es.jmjg.experiments.application.post.dto.SavePostCommentDto;
import es.jmjg.experiments.domain.post.entity.Post;
import es.jmjg.experiments.domain.post.entity.PostComment;
import es.jmjg.experiments.domain.user.entity.User;
import es.jmjg.experiments.infrastructure.repository.UserRepositoryImpl;
import es.jmjg.experiments.infrastructure.repository.jpa.PostCommentRepositoryImpl;
import es.jmjg.experiments.shared.BaseIntegration;
import es.jmjg.experiments.shared.PostFactory;
import es.jmjg.experiments.shared.TestDataSamples;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

class SavePostCommentIntegrationTest extends BaseIntegration {

  @Autowired
  private SavePostComment savePostComment;

  @Autowired
  private UserRepositoryImpl userRepository;

  @Autowired
  private PostCommentRepositoryImpl postCommentRepository;

  @Test
  @Transactional
  void save_ShouldSaveAndReturnPostComment() {
    // Given
    User testUser = userRepository.findByUuid(TestDataSamples.LEANNE_UUID).orElseThrow();
    Post testPost = testUser.getPosts().getFirst();
    String comment = "This is a test comment";
    PostComment postComment = PostFactory.createPostComment(testUser, testPost, comment);
    SavePostCommentDto dto = PostFactory.createSavePostCommentDto(testUser, postComment.getUuid(), testPost, postComment.getComment());

    // When
    PostComment result = savePostComment.save(dto);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getUuid()).isEqualTo(postComment.getUuid());
    assertThat(result.getPost().getUuid()).isEqualTo(postComment.getPost().getUuid());
    assertThat(result.getUser().getUuid()).isEqualTo(postComment.getUser().getUuid());
    assertThat(result.getComment()).isEqualTo(postComment.getComment());

    // Verify it was actually saved to the database
    Optional<PostComment> savedPostComment = postCommentRepository.findByUuid(result.getUuid());
    assertThat(savedPostComment).isPresent();
    assertThat(savedPostComment.get().getId()).isEqualTo(result.getId());
    assertThat(savedPostComment.get().getComment()).isEqualTo(result.getComment());
  }
}
