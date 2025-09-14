package es.jmjg.experiments.application.post;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import es.jmjg.experiments.shared.PostFactory;
import es.jmjg.experiments.shared.UserFactory;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SavePostCommentTest {

  @Mock
  private PostRepository postRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private PostCommentRepository postCommentRepository;

  @InjectMocks
  private SavePostComment savePostComment;

  private User testUser;

  private Post testPost;

  @BeforeEach
  void setUp() {
    testUser = UserFactory.createBasicUser();
    var postUser = UserFactory.createBasicUser();
    testPost = PostFactory.createBasicPost(postUser);
  }

  @Test
  void save_ShouldSaveAndReturnPost() {
    // Given
    String postComment = "This is a test comment";
    PostComment newPostComment = PostFactory.createPostComment(testUser, testPost, postComment);

    when(userRepository.findByUuid(testUser.getUuid())).thenReturn(Optional.of(testUser));
    when(postRepository.findByUuid(testPost.getUuid())).thenReturn(Optional.of(testPost));
    when(postCommentRepository.save(any(PostComment.class))).thenReturn(newPostComment);

    // When
    SavePostCommentDto dto = PostFactory.createSavePostCommentDto(testUser, newPostComment.getUuid(), newPostComment.getPost(), newPostComment.getComment());
    PostComment result = savePostComment.save(dto);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getComment()).isEqualTo(newPostComment.getComment());
    assertThat(result.getUser()).isEqualTo(newPostComment.getUser());
    assertThat(result.getPost()).isEqualTo(newPostComment.getPost());

    verify(userRepository, times(1)).findByUuid(testUser.getUuid());
    verify(postRepository, times(1)).findByUuid(testPost.getUuid());
    verify(postCommentRepository, times(1)).save(any(PostComment.class));
  }

  @Test
  void whenUserIsNotFound_ShouldThrowInvalidRequest() {
    // Given
    String postComment = "This is a test comment";
    PostComment newPostComment = PostFactory.createPostComment(testUser, testPost, postComment);

    when(userRepository.findByUuid(testUser.getUuid())).thenReturn(Optional.empty());

    // When
    SavePostCommentDto dto = PostFactory.createSavePostCommentDto(testUser, newPostComment.getUuid(), newPostComment.getPost(), newPostComment.getComment());
    assertThatThrownBy(() -> savePostComment.save(dto)).isInstanceOf(UserNotFound.class);

    verify(postCommentRepository, times(0)).save(any(PostComment.class));
  }

  @Test
  void whenPostIsNotFound_ShouldThrowNotFound() {
    // Given
    String postComment = "This is a test comment";
    PostComment newPostComment = PostFactory.createPostComment(testUser, testPost, postComment);

    when(userRepository.findByUuid(testUser.getUuid())).thenReturn(Optional.of(testUser));
    when(postRepository.findByUuid(testPost.getUuid())).thenReturn(Optional.empty());

    // When
    SavePostCommentDto dto = PostFactory.createSavePostCommentDto(testUser, newPostComment.getUuid(), newPostComment.getPost(), newPostComment.getComment());
    assertThatThrownBy(() -> savePostComment.save(dto)).isInstanceOf(PostNotFound.class);

    verify(postCommentRepository, times(0)).save(any(PostComment.class));
  }

  @Test
  void whenCommentIsEmpty_ShouldThrowInvalidRequest() {
    // Given
    String postComment = "";
    PostComment newPostComment = PostFactory.createPostComment(testUser, testPost, postComment);

    when(userRepository.findByUuid(testUser.getUuid())).thenReturn(Optional.of(testUser));
    when(postRepository.findByUuid(testPost.getUuid())).thenReturn(Optional.of(testPost));

    // When
    SavePostCommentDto dto = PostFactory.createSavePostCommentDto(testUser, newPostComment.getUuid(), newPostComment.getPost(), newPostComment.getComment());
    assertThatThrownBy(() -> savePostComment.save(dto))
      .isInstanceOf(InvalidRequest.class)
      .hasMessage("Comment cannot be empty");

    verify(postCommentRepository, times(0)).save(any(PostComment.class));
  }
}
