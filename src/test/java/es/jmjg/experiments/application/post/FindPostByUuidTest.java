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

import es.jmjg.experiments.domain.entity.Post;
import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.infrastructure.repository.PostRepository;

@ExtendWith(MockitoExtension.class)
class FindPostByUuidTest {

  @Mock private PostRepository postRepository;

  @InjectMocks private FindPostByUuid findPostByUuid;

  private Post testPost;
  private User testUser;
  private UUID testUuid;

  @BeforeEach
  void setUp() {
    testUser = new User(1, UUID.randomUUID(), "Test User", "test@example.com", "testuser", null);
    testUuid = UUID.randomUUID();
    testPost = new Post(1, testUuid, testUser, "Test Post", "Test Body");
  }

  @Test
  void findByUuid_WhenPostExists_ShouldReturnPost() {
    // Given
    when(postRepository.findByUuid(testUuid)).thenReturn(Optional.of(testPost));

    // When
    Optional<Post> result = findPostByUuid.findByUuid(testUuid);

    // Then
    assertThat(result).isPresent();
    assertThat(result.get()).isEqualTo(testPost);
    verify(postRepository, times(1)).findByUuid(testUuid);
  }

  @Test
  void findByUuid_WhenPostDoesNotExist_ShouldReturnEmpty() {
    // Given
    UUID nonExistentUuid = UUID.randomUUID();
    when(postRepository.findByUuid(nonExistentUuid)).thenReturn(Optional.empty());

    // When
    Optional<Post> result = findPostByUuid.findByUuid(nonExistentUuid);

    // Then
    assertThat(result).isEmpty();
    verify(postRepository, times(1)).findByUuid(nonExistentUuid);
  }

  @Test
  void findByUuid_WhenUuidIsNull_ShouldReturnEmpty() {
    // When
    Optional<Post> result = findPostByUuid.findByUuid(null);

    // Then
    assertThat(result).isEmpty();
    verify(postRepository, never()).findByUuid(any());
  }
}
