package es.jmjg.experiments.application.post;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import es.jmjg.experiments.domain.post.entity.Post;
import es.jmjg.experiments.domain.post.entity.PostComment;
import es.jmjg.experiments.domain.post.exception.PostCommentNotFound;
import es.jmjg.experiments.domain.post.repository.PostCommentRepository;
import es.jmjg.experiments.shared.PostFactory;
import es.jmjg.experiments.shared.UserFactory;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FindPostCommentByUuidTest {

  @Mock
  private PostCommentRepository postCommentRepository;

  @InjectMocks
  private FindPostCommentByUuid findPostCommentByUuid;

  private Post testPost;
  private PostComment testPostComment;

  @BeforeEach
  void setUp() {
    var testUser = UserFactory.createBasicUser();
    testPost = PostFactory.createBasicPost(testUser);
    testPostComment = PostFactory.createPostComment(testUser, testPost, "Test comment");
  }

  @Test
  void findByUuid_WhenPostCommentExists_ShouldReturnPostComment() {
    // Given
    when(postCommentRepository.findByUuid(testPostComment.getUuid())).thenReturn(Optional.of(testPostComment));

    // When
    PostComment result = findPostCommentByUuid.findByUuid(testPost.getUuid(), testPostComment.getUuid());

    // Then
    assertThat(result).isEqualTo(testPostComment);
    verify(postCommentRepository, times(1)).findByUuid(testPostComment.getUuid());
  }

  @Test
  void findByUuid_WhenPostCommentDoesNotExist_ShouldThrowPostCommentNotFoundException() {
    // Given
    var nonExistentUuid = UUID.randomUUID();
    when(postCommentRepository.findByUuid(nonExistentUuid)).thenReturn(Optional.empty());

    // When
    assertThatThrownBy(() -> findPostCommentByUuid.findByUuid(testPost.getUuid(), nonExistentUuid))
        .isInstanceOf(PostCommentNotFound.class)
        .hasMessage("Post comment with id " + nonExistentUuid + " not found");

    // Then
    verify(postCommentRepository, times(1)).findByUuid(nonExistentUuid);
  }

  @Test
  void findByUuid_WhenPostCommentAndPostDoNotMatch_ShouldThrowPostCommentNotFoundException() {
    // Given
    var notMatchingPostUuid = UUID.randomUUID();
    when(postCommentRepository.findByUuid(testPostComment.getUuid())).thenReturn(Optional.of(testPostComment));

    // When
    assertThatThrownBy(() -> findPostCommentByUuid.findByUuid(notMatchingPostUuid, testPostComment.getUuid()))
      .isInstanceOf(PostCommentNotFound.class)
      .hasMessage("Post comment with id " + testPostComment.getUuid() + " not found for post with id " + notMatchingPostUuid);

    // Then
    verify(postCommentRepository, times(1)).findByUuid(testPostComment.getUuid());
  }

  @Test
  void findByUuid_WhenUuidIsNull_ShouldThrowIllegalArgumentException() {
    // When
    assertThatThrownBy(() -> findPostCommentByUuid.findByUuid(null, null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("post id cannot be null");

    // Then
    verify(postCommentRepository, never()).findByUuid(null);
  }
}
