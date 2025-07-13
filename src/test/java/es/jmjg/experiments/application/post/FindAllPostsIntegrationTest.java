package es.jmjg.experiments.application.post;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

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
import es.jmjg.experiments.shared.PostFactory;
import es.jmjg.experiments.shared.UserFactory;

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
    testUser = userRepository.save(UserFactory.createBasicUser());

    // Create test posts
    testPost1 = PostFactory.createBasicPost(testUser);
    testPost2 = PostFactory.createPost(testUser, "Another Post", "Another Body");
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
    assertThat(result)
        .extracting("title")
        .containsExactlyInAnyOrder(testPost1.getTitle(), testPost2.getTitle());
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
