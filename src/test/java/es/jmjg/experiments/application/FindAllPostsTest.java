package es.jmjg.experiments.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.jmjg.experiments.application.post.FindAllPosts;
import es.jmjg.experiments.domain.Post;
import es.jmjg.experiments.domain.User;
import es.jmjg.experiments.infrastructure.repository.PostRepository;

@ExtendWith(MockitoExtension.class)
class FindAllPostsTest {

  @Mock private PostRepository postRepository;

  @InjectMocks private FindAllPosts findAllPosts;

  private Post testPost1;
  private Post testPost2;
  private List<Post> testPosts;
  private User testUser;
  private UUID testUuid1;
  private UUID testUuid2;

  @BeforeEach
  void setUp() {
    testUser = new User(1, UUID.randomUUID(), "Test User", "test@example.com", "testuser", null);
    testUuid1 = UUID.randomUUID();
    testUuid2 = UUID.randomUUID();
    testPost1 = new Post(1, testUuid1, testUser, "Test Post 1", "Test Body 1");
    testPost2 = new Post(2, testUuid2, testUser, "Test Post 2", "Test Body 2");
    testPosts = Arrays.asList(testPost1, testPost2);
  }

  @Test
  void findAll_ShouldReturnAllPosts() {
    // Given
    when(postRepository.findAll()).thenReturn(testPosts);

    // When
    List<Post> result = findAllPosts.findAll();

    // Then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(2);
    assertThat(result).containsExactly(testPost1, testPost2);
    verify(postRepository, times(1)).findAll();
  }

  @Test
  void findAll_WhenNoPosts_ShouldReturnEmptyList() {
    // Given
    when(postRepository.findAll()).thenReturn(List.of());

    // When
    List<Post> result = findAllPosts.findAll();

    // Then
    assertThat(result).isNotNull();
    assertThat(result).isEmpty();
    verify(postRepository, times(1)).findAll();
  }
}
