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

import es.jmjg.experiments.application.post.exception.PostNotFound;
import es.jmjg.experiments.application.user.exception.UserNotFound;
import es.jmjg.experiments.domain.entity.Post;
import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.domain.repository.PostRepository;
import es.jmjg.experiments.domain.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UpdatePostTest {

  @Mock
  private PostRepository postRepository;

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private UpdatePost updatePost;

  private User testUser;
  private Post testPost;

  @BeforeEach
  void setUp() {
    testUser = new User(1, UUID.randomUUID(), "Test User", "test@example.com", "testuser", null);
    testPost = new Post(1, UUID.randomUUID(), testUser, "Test Post", "Test Body");
  }

  @Test
  void update_WhenPostExists_ShouldUpdatePost() {
    // Given
    Integer postId = 1;
    Post updateData = new Post(null, UUID.randomUUID(), testUser, "Updated Title", "Updated Body");

    when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));
    when(postRepository.save(any(Post.class)))
        .thenAnswer(
            invocation -> {
              Post savedPost = invocation.getArgument(0);
              return savedPost;
            });

    // When
    Post result = updatePost.update(postId, updateData);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getTitle()).isEqualTo("Updated Title");
    assertThat(result.getBody()).isEqualTo("Updated Body");

    verify(postRepository, times(1)).findById(postId);
    verify(postRepository, times(1)).save(any(Post.class));
  }

  @Test
  void update_WhenPostDoesNotExist_ShouldThrowPostNotFound() {
    // Given
    Integer postId = 999;
    Post updateData = new Post(null, UUID.randomUUID(), testUser, "Updated Title", "Updated Body");

    when(postRepository.findById(postId)).thenReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> updatePost.update(postId, updateData))
        .isInstanceOf(PostNotFound.class)
        .hasMessage("Post not found with id: " + postId);

    verify(postRepository, times(1)).findById(postId);
    verify(postRepository, never()).save(any());
  }

  @Test
  void update_ShouldPreserveOriginalIdAndUserId() {
    // Given
    Integer postId = 1;
    User user = new User(5, UUID.randomUUID(), "Test User", "test@example.com", "testuser", null);
    UUID existingUuid = UUID.randomUUID();
    UUID updateUuid = UUID.randomUUID();
    Post existingPost = new Post(1, existingUuid, user, "Original Title", "Original Body");
    Post updateData = new Post(999, updateUuid, user, "Updated Title", "Updated Body"); // Different
    // ID

    when(postRepository.findById(postId)).thenReturn(Optional.of(existingPost));
    when(postRepository.save(any(Post.class)))
        .thenAnswer(
            invocation -> {
              Post savedPost = invocation.getArgument(0);
              return savedPost;
            });

    // When
    Post result = updatePost.update(postId, updateData);

    // Then
    assertThat(result.getId()).isEqualTo(1); // Should preserve original ID
    assertThat(result.getUser().getId()).isEqualTo(5); // Should preserve original userId
    assertThat(result.getTitle()).isEqualTo("Updated Title"); // Should update title
    assertThat(result.getBody()).isEqualTo("Updated Body"); // Should update body

    verify(postRepository, times(1)).findById(postId);
    verify(postRepository, times(1)).save(any(Post.class));
  }

  @Test
  void update_WhenUserIdProvidedAndUserExists_ShouldUpdateUser() {
    // Given
    Integer postId = 1;
    UUID userUuid = UUID.randomUUID();
    User user = new User(5, userUuid, "Test User", "test@example.com", "testuser", null);
    Post updateData = new Post(null, UUID.randomUUID(), testUser, "Updated Title", "Updated Body");

    when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));
    when(userRepository.findByUuid(userUuid)).thenReturn(Optional.of(user));
    when(postRepository.save(any(Post.class)))
        .thenAnswer(
            invocation -> {
              Post savedPost = invocation.getArgument(0);
              return savedPost;
            });

    // When
    Post result = updatePost.update(postId, updateData, userUuid);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getUser()).isEqualTo(user);
    assertThat(result.getTitle()).isEqualTo("Updated Title");
    assertThat(result.getBody()).isEqualTo("Updated Body");

    verify(postRepository, times(1)).findById(postId);
    verify(userRepository, times(1)).findByUuid(userUuid);
    verify(postRepository, times(1)).save(any(Post.class));
  }

  @Test
  void update_WhenUserIdProvidedButUserNotFound_ShouldThrowUserNotFound() {
    // Given
    Integer postId = 1;
    UUID nonExistentUserUuid = UUID.randomUUID();
    Post updateData = new Post(null, UUID.randomUUID(), testUser, "Updated Title", "Updated Body");

    when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));
    when(userRepository.findByUuid(nonExistentUserUuid)).thenReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> updatePost.update(postId, updateData, nonExistentUserUuid))
        .isInstanceOf(UserNotFound.class)
        .hasMessage("User not found with uuid: " + nonExistentUserUuid);

    verify(postRepository, times(1)).findById(postId);
    verify(userRepository, times(1)).findByUuid(nonExistentUserUuid);
    verify(postRepository, never()).save(any());
  }

  @Test
  void update_WhenPostHasUser_ShouldUsePostUser() {
    // Given
    Integer postId = 1;
    User postUser = new User(3, UUID.randomUUID(), "Post User", "post@example.com", "postuser", null);
    UUID updateUuid = UUID.randomUUID();
    Post updateData = new Post(null, updateUuid, postUser, "Updated Title", "Updated Body");

    when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));
    when(postRepository.save(any(Post.class)))
        .thenAnswer(
            invocation -> {
              Post savedPost = invocation.getArgument(0);
              return savedPost;
            });

    // When
    Post result = updatePost.update(postId, updateData);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getUser()).isEqualTo(postUser);
    assertThat(result.getTitle()).isEqualTo("Updated Title");
    assertThat(result.getBody()).isEqualTo("Updated Body");

    verify(postRepository, times(1)).findById(postId);
    verify(userRepository, never()).findById(any());
    verify(postRepository, times(1)).save(any(Post.class));
  }

  @Test
  void update_WhenUuidProvided_ShouldUpdateUuid() {
    // Given
    Integer postId = 1;
    UUID newUuid = UUID.randomUUID();
    Post updateData = new Post(null, newUuid, testUser, "Updated Title", "Updated Body");

    when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));
    when(postRepository.save(any(Post.class)))
        .thenAnswer(
            invocation -> {
              Post savedPost = invocation.getArgument(0);
              return savedPost;
            });

    // When
    Post result = updatePost.update(postId, updateData);

    // Then
    assertThat(result.getUuid()).isEqualTo(newUuid);
    verify(postRepository, times(1)).findById(postId);
    verify(postRepository, times(1)).save(any(Post.class));
  }

  @Test
  void update_WhenUuidNotProvided_ShouldPreserveOriginalUuid() {
    // Given
    Integer postId = 1;
    UUID originalUuid = UUID.randomUUID();
    Post existingPost = new Post(1, originalUuid, testUser, "Original Title", "Original Body");
    Post updateData = new Post(null, null, testUser, "Updated Title", "Updated Body");

    when(postRepository.findById(postId)).thenReturn(Optional.of(existingPost));
    when(postRepository.save(any(Post.class)))
        .thenAnswer(
            invocation -> {
              Post savedPost = invocation.getArgument(0);
              return savedPost;
            });

    // When
    Post result = updatePost.update(postId, updateData);

    // Then
    assertThat(result.getUuid()).isEqualTo(originalUuid);
    verify(postRepository, times(1)).findById(postId);
    verify(postRepository, times(1)).save(any(Post.class));
  }
}
