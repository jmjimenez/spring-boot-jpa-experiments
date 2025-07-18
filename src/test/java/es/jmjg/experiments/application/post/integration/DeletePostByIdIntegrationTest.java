package es.jmjg.experiments.application.post.integration;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import es.jmjg.experiments.application.post.DeletePostById;
import es.jmjg.experiments.domain.entity.Post;
import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.infrastructure.config.TestContainersConfig;
import es.jmjg.experiments.infrastructure.repository.PostRepository;
import es.jmjg.experiments.infrastructure.repository.UserRepository;
import es.jmjg.experiments.shared.PostFactory;
import es.jmjg.experiments.shared.UserFactory;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class DeletePostByIdIntegrationTest extends TestContainersConfig {

  @Autowired
  private DeletePostById deletePostById;

  @Autowired
  private PostRepository postRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private Environment environment;

  private User testUser;
  private Post testPost1;

  @BeforeEach
  void setUp() {
    // Clear the database before each test
    // Delete in order to respect foreign key constraints
    postRepository.deleteAll();
    userRepository.deleteAll();

    // Create a test user
    testUser = userRepository.save(UserFactory.createBasicUser());

    // Create test posts associated with the user
    testPost1 = PostFactory.createBasicPost(testUser);
  }

  @Test
  void shouldUseTestProfile() {
    // Verify that the test profile is active
    String[] activeProfiles = environment.getActiveProfiles();
    assertThat(activeProfiles).contains("test");
  }

  // TODO: this test is repeated in other tests, we should refactor it
  @Test
  void connectionEstablished() {
    assertThat(TestContainersConfig.getPostgresContainer().isCreated()).isTrue();
    assertThat(TestContainersConfig.getPostgresContainer().isRunning()).isTrue();
  }

  @Test
  void deleteById_ShouldDeletePost() {
    // Given
    Post savedPost = postRepository.save(testPost1);
    assertThat(postRepository.findById(savedPost.getId())).isPresent();

    // When
    deletePostById.deleteById(savedPost.getId());

    // Then
    assertThat(postRepository.findById(savedPost.getId())).isEmpty();
  }

  @Test
  void deleteById_WhenPostDoesNotExist_ShouldNotThrowException() {
    // When & Then - should not throw any exception
    deletePostById.deleteById(999);
  }
}
