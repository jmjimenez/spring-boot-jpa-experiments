package es.jmjg.experiments.application;

import static org.assertj.core.api.Assertions.*;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import es.jmjg.experiments.application.post.FindAllPosts;
import es.jmjg.experiments.domain.Post;
import es.jmjg.experiments.domain.User;
import es.jmjg.experiments.infrastructure.config.TestContainersConfig;
import es.jmjg.experiments.infrastructure.repository.PostRepository;
import es.jmjg.experiments.infrastructure.repository.UserRepository;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class FindAllPostsIntegrationTest extends TestContainersConfig {

  @Autowired
  private FindAllPosts findAllPosts;

  @Autowired
  private PostRepository postRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private Environment environment;

  private User testUser;
  private Post testPost1;
  private Post testPost2;

  @BeforeEach
  void setUp() {
    // Clear the database before each test
    postRepository.deleteAll();
    userRepository.deleteAll();

    // Create a test user
    testUser = new User();
    testUser.setUuid(UUID.randomUUID());
    testUser.setName("Test User");
    testUser.setEmail("test@example.com");
    testUser.setUsername("testuser");
    testUser = userRepository.save(testUser);

    // Create test posts associated with the user
    testPost1 = new Post();
    testPost1.setUuid(UUID.randomUUID());
    testPost1.setUser(testUser);
    testPost1.setTitle("Test Post 1");
    testPost1.setBody("Test Body 1");

    testPost2 = new Post();
    testPost2.setUuid(UUID.randomUUID());
    testPost2.setUser(testUser);
    testPost2.setTitle("Test Post 2");
    testPost2.setBody("Test Body 2");
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
  void findAll_ShouldReturnAllPosts() {
    // Given
    postRepository.save(testPost1);
    postRepository.save(testPost2);

    // When
    List<Post> result = findAllPosts.findAll();

    // Then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(2);
    assertThat(result).extracting("title").containsExactlyInAnyOrder("Test Post 1", "Test Post 2");
  }

  @Test
  void findAll_WhenNoPosts_ShouldReturnEmptyList() {
    // When
    List<Post> result = findAllPosts.findAll();

    // Then
    assertThat(result).isNotNull();
    assertThat(result).isEmpty();
  }
}
