package es.jmjg.experiments.application.post.integration;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import es.jmjg.experiments.application.post.FindPostByTitle;
import es.jmjg.experiments.domain.Post;
import es.jmjg.experiments.domain.User;
import es.jmjg.experiments.infrastructure.config.TestContainersConfig;
import es.jmjg.experiments.infrastructure.repository.PostRepository;
import es.jmjg.experiments.infrastructure.repository.UserRepository;
import es.jmjg.experiments.shared.PostFactory;
import es.jmjg.experiments.shared.UserFactory;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class FindPostByTitleIntegrationTest extends TestContainersConfig {

  @Autowired
  private FindPostByTitle findPostByTitle;

  @Autowired
  private PostRepository postRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private Environment environment;

  private User testUser;
  private Post testPost;

  @BeforeEach
  void setUp() {
    // Clear the database before each test
    postRepository.deleteAll();
    userRepository.deleteAll();

    // Create a test user
    testUser = userRepository.save(UserFactory.createBasicUser());

    // Create test post
    testPost = PostFactory.createBasicPost(testUser);
  }

  @Test
  void shouldUseTestProfile() {
    // Verify that the test profile is active
    String[] activeProfiles = environment.getActiveProfiles();
    assertThat(activeProfiles).contains("test");
  }

  @Test
  void connectionEstablished() {
    assertThat(TestContainersConfig.getPostgresContainer().isCreated()).isTrue();
    assertThat(TestContainersConfig.getPostgresContainer().isRunning()).isTrue();
  }

  @Test
  void findByTitle_WhenTitleExists_ShouldReturnPost() {
    // Given
    postRepository.save(testPost);

    // When
    Optional<Post> result = findPostByTitle.findByTitle("Test Post");

    // Then
    assertThat(result).isPresent();
    assertThat(result.get().getTitle()).isEqualTo("Test Post");
    assertThat(result.get().getBody()).isEqualTo("Test Body");
    assertThat(result.get().getUser().getId()).isEqualTo(testUser.getId());
  }

  @Test
  void findByTitle_WhenTitleDoesNotExist_ShouldReturnEmpty() {
    // Given
    postRepository.save(testPost);

    // When
    Optional<Post> result = findPostByTitle.findByTitle("Non-existent Post");

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void findByTitle_WhenTitleIsNull_ShouldReturnEmpty() {
    // Given
    postRepository.save(testPost);

    // When
    Optional<Post> result = findPostByTitle.findByTitle(null);

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void findByTitle_WhenTitleIsEmpty_ShouldReturnEmpty() {
    // Given
    postRepository.save(testPost);

    // When
    Optional<Post> result = findPostByTitle.findByTitle("");

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void findByTitle_WhenTitleIsWhitespace_ShouldReturnEmpty() {
    // Given
    postRepository.save(testPost);

    // When
    Optional<Post> result = findPostByTitle.findByTitle("   ");

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void findByTitle_WhenTitleIsUnique_ShouldReturnPost() {
    // Given
    Post savedPost = postRepository.save(testPost);

    // When
    Optional<Post> result = findPostByTitle.findByTitle(testPost.getTitle());

    // Then
    assertThat(result).isPresent();
    assertThat(result.get().getTitle()).isEqualTo(testPost.getTitle());
    assertThat(result.get().getId()).isEqualTo(savedPost.getId());
  }
}
