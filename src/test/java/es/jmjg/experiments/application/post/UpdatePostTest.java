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
  private UUID testPostUuid;

  @BeforeEach
  void setUp() {
    testPostUuid = UUID.randomUUID();
    testUser = new User(1, UUID.randomUUID(), "Test User", "test@example.com", "testuser", "encodedPassword123", null);
    testPost = new Post(1, testPostUuid, testUser, "Test Post", "Test Body");
  }

  @Test
  void update_WhenPostExists_ShouldUpdatePost() {
    // Given
    Post updateData = new Post(null, testPostUuid, testUser, "Updated Title", "Updated Body");

    when(postRepository.findByUuid(testPostUuid)).thenReturn(Optional.of(testPost));
    when(postRepository.save(any(Post.class)))
        .thenAnswer(
            invocation -> {
              Post savedPost = invocation.getArgument(0);
              return savedPost;
            });

    // When
    Post result = updatePost.update(testPostUuid, updateData);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getTitle()).isEqualTo("Updated Title");
    assertThat(result.getBody()).isEqualTo("Updated Body");
    assertThat(result.getTags()).isNotNull(); // Tags field should be present

    verify(postRepository, times(1)).findByUuid(testPostUuid);
    verify(postRepository, times(1)).save(any(Post.class));
  }

  @Test
  void update_WhenPostDoesNotExist_ShouldThrowPostNotFound() {
    // Given
    UUID nonExistentPostUuid = UUID.randomUUID();
    Post updateData = new Post(null, nonExistentPostUuid, testUser, "Updated Title", "Updated Body");

    when(postRepository.findByUuid(nonExistentPostUuid)).thenReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> updatePost.update(nonExistentPostUuid, updateData))
        .isInstanceOf(PostNotFound.class)
        .hasMessage("Post not found with uuid: " + nonExistentPostUuid);

    verify(postRepository, times(1)).findByUuid(nonExistentPostUuid);
    verify(postRepository, never()).save(any());
  }

  @Test
  void update_ShouldPreserveOriginalUuidAndUserId() {
    // Given
    User updatedUser = new User(5, UUID.randomUUID(), "Test User", "test@example.com", "testuser", "encodedPassword123",
        null);
    Post updateData = new Post(999, UUID.randomUUID(), updatedUser, "Updated Title", "Updated Body");

    when(postRepository.findByUuid(testPostUuid)).thenReturn(Optional.of(testPost));
    when(postRepository.save(any(Post.class)))
        .thenAnswer(
            invocation -> {
              Post savedPost = invocation.getArgument(0);
              return savedPost;
            });

    // When
    Post result = updatePost.update(testPostUuid, updateData);

    // Then
    assertThat(result.getUuid()).isEqualTo(testPostUuid);
    assertThat(result.getUser().getUuid()).isEqualTo(testUser.getUuid());
    assertThat(result.getTitle()).isEqualTo("Updated Title");
    assertThat(result.getBody()).isEqualTo("Updated Body");
    assertThat(result.getTags()).isNotNull();

    verify(postRepository, times(1)).findByUuid(testPostUuid);
    verify(postRepository, times(1)).save(any(Post.class));
  }

  @Test
  void update_WhenPostHasUser_ShouldNotUsePostUser() {
    // Given
    User postUser = new User(3, UUID.randomUUID(), "Post User", "post@example.com", "postuser", "encodedPassword123",
        null);
    UUID updateUuid = UUID.randomUUID();
    Post updateData = new Post(null, updateUuid, postUser, "Updated Title", "Updated Body");

    when(postRepository.findByUuid(testPostUuid)).thenReturn(Optional.of(testPost));
    when(postRepository.save(any(Post.class)))
        .thenAnswer(
            invocation -> {
              Post savedPost = invocation.getArgument(0);
              return savedPost;
            });

    // When
    Post result = updatePost.update(testPostUuid, updateData);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getUser()).isEqualTo(testUser);
    assertThat(result.getTitle()).isEqualTo("Updated Title");
    assertThat(result.getBody()).isEqualTo("Updated Body");
    assertThat(result.getTags()).isNotNull(); // Tags field should be present

    verify(postRepository, times(1)).findByUuid(testPostUuid);
    verify(userRepository, never()).findById(any());
    verify(postRepository, times(1)).save(any(Post.class));
  }

  @Test
  void update_WhenUuidProvided_ShouldNotUpdateUuid() {
    // Given
    UUID newUuid = UUID.randomUUID();
    Post updateData = new Post(null, newUuid, testUser, "Updated Title", "Updated Body");

    when(postRepository.findByUuid(testPostUuid)).thenReturn(Optional.of(testPost));
    when(postRepository.save(any(Post.class)))
        .thenAnswer(
            invocation -> {
              Post savedPost = invocation.getArgument(0);
              return savedPost;
            });

    // When
    Post result = updatePost.update(testPostUuid, updateData);

    // Then
    assertThat(result.getUuid()).isEqualTo(testPostUuid);
    assertThat(result.getTags()).isNotNull(); // Tags field should be present
    verify(postRepository, times(1)).findByUuid(testPostUuid);
    verify(postRepository, times(1)).save(any(Post.class));
  }

  @Test
  void update_WhenUuidNotProvided_ShouldPreserveOriginalUuid() {
    // Given
    UUID originalUuid = UUID.randomUUID();
    Post existingPost = new Post(1, originalUuid, testUser, "Original Title", "Original Body");
    Post updateData = new Post(null, null, testUser, "Updated Title", "Updated Body");

    when(postRepository.findByUuid(testPostUuid)).thenReturn(Optional.of(existingPost));
    when(postRepository.save(any(Post.class)))
        .thenAnswer(
            invocation -> {
              Post savedPost = invocation.getArgument(0);
              return savedPost;
            });

    // When
    Post result = updatePost.update(testPostUuid, updateData);

    // Then
    assertThat(result.getUuid()).isEqualTo(originalUuid);
    assertThat(result.getTags()).isNotNull(); // Tags field should be present
    verify(postRepository, times(1)).findByUuid(testPostUuid);
    verify(postRepository, times(1)).save(any(Post.class));
  }
}
