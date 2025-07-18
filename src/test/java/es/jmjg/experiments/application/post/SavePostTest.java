package es.jmjg.experiments.application.post;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.jmjg.experiments.application.post.exception.InvalidRequest;
import es.jmjg.experiments.application.user.exception.UserNotFound;
import es.jmjg.experiments.domain.entity.Post;
import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.infrastructure.repository.PostRepository;
import es.jmjg.experiments.infrastructure.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class SavePostTest {

  @Mock
  private PostRepository postRepository;

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private SavePost savePost;

  private User testUser;

  @BeforeEach
  void setUp() {
    testUser = new User(1, UUID.randomUUID(), "Test User", "test@example.com", "testuser", null);
  }

  @Test
  void save_ShouldSaveAndReturnPost() {
    // Given
    UUID newUuid = UUID.randomUUID();
    Post newPost = new Post(null, newUuid, testUser, "New Post", "New Body");
    Post savedPost = new Post(3, newUuid, testUser, "New Post", "New Body");

    when(postRepository.save(any(Post.class))).thenReturn(savedPost);

    // When
    Post result = savePost.save(newPost);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(3);
    assertThat(result.getTitle()).isEqualTo("New Post");
    assertThat(result.getBody()).isEqualTo("New Body");
    verify(postRepository, times(1)).save(newPost);
  }

  @Test
  void save_WhenPostAlreadyHasUser_ShouldNotFetchUser() {
    // Given
    UUID newUuid = UUID.randomUUID();
    Post newPost = new Post(null, newUuid, testUser, "New Post", "New Body");
    Post savedPost = new Post(3, newUuid, testUser, "New Post", "New Body");

    when(postRepository.save(any(Post.class))).thenReturn(savedPost);

    // When
    Post result = savePost.save(newPost);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(3);
    verify(userRepository, never()).findById(any());
    verify(postRepository, times(1)).save(newPost);
  }

  @Test
  void save_WhenPostHasNoUser_ShouldThrowInvalidRequest() {
    // Given
    UUID newUuid = UUID.randomUUID();
    Post newPost = new Post(null, newUuid, null, "New Post", "New Body");

    // When & Then
    assertThatThrownBy(() -> savePost.save(newPost))
        .isInstanceOf(InvalidRequest.class)
        .hasMessage("Post must have a user");

    verify(userRepository, never()).findById(any());
    verify(postRepository, never()).save(any());
  }

  @Test
  void save_WhenUserIdProvidedAndUserExists_ShouldSetUserAndSave() {
    // Given
    UUID userUuid = testUser.getUuid();
    UUID newUuid = UUID.randomUUID();
    Post newPost = new Post(null, newUuid, null, "New Post", "New Body");
    Post savedPost = new Post(3, newUuid, testUser, "New Post", "New Body");

    when(userRepository.findByUuid(userUuid)).thenReturn(Optional.of(testUser));
    when(postRepository.save(any(Post.class))).thenReturn(savedPost);

    // When
    Post result = savePost.save(newPost, userUuid);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(3);
    assertThat(result.getTitle()).isEqualTo("New Post");
    assertThat(result.getBody()).isEqualTo("New Body");
    assertThat(result.getUser()).isEqualTo(testUser);

    verify(userRepository, times(1)).findByUuid(userUuid);
    verify(postRepository, times(1)).save(any(Post.class));
  }

  @Test
  void save_WhenUserIdProvidedButUserNotFound_ShouldThrowUserNotFound() {
    // Given
    UUID nonExistentUserUuid = UUID.randomUUID();
    UUID newUuid = UUID.randomUUID();
    Post newPost = new Post(null, newUuid, null, "New Post", "New Body");

    when(userRepository.findByUuid(nonExistentUserUuid)).thenReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> savePost.save(newPost, nonExistentUserUuid))
        .isInstanceOf(UserNotFound.class)
        .hasMessage("User not found with uuid: " + nonExistentUserUuid);

    verify(userRepository, times(1)).findByUuid(nonExistentUserUuid);
    verify(postRepository, never()).save(any());
  }
}
