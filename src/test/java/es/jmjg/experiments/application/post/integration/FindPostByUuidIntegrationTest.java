package es.jmjg.experiments.application.post.integration;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import es.jmjg.experiments.application.post.FindPostByUuid;
import es.jmjg.experiments.domain.entity.Post;
import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.infrastructure.config.TestContainersConfig;
import es.jmjg.experiments.infrastructure.repository.PostRepository;
import es.jmjg.experiments.infrastructure.repository.UserRepository;
import es.jmjg.experiments.shared.PostFactory;
import es.jmjg.experiments.shared.UserFactory;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class FindPostByUuidIntegrationTest extends TestContainersConfig {

  @Autowired
  private FindPostByUuid findPostByUuid;

  @Autowired
  private PostRepository postRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private Environment environment;

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
  void findByUuid_WhenPostExists_ShouldReturnPost() {
    // Given
    User testUser = userRepository.save(UserFactory.createBasicUser());
    Post testPost = PostFactory.createBasicPost(testUser);
    postRepository.save(testPost);

    // When
    Optional<Post> result = findPostByUuid.findByUuid(testPost.getUuid());

    // Then
    assertThat(result).isPresent();
    assertThat(result.get().getTitle()).isEqualTo(testPost.getTitle());
    assertThat(result.get().getBody()).isEqualTo(testPost.getBody());
    assertThat(result.get().getUser().getId()).isEqualTo(testUser.getId());
    assertThat(result.get().getUuid()).isEqualTo(testPost.getUuid());
  }

  @Test
  void findByUuid_WhenPostDoesNotExist_ShouldReturnEmpty() {
    // When
    Optional<Post> result = findPostByUuid.findByUuid(UUID.randomUUID());

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void findByUuid_WhenUuidIsNull_ShouldReturnEmpty() {
    // When
    Optional<Post> result = findPostByUuid.findByUuid(null);

    // Then
    assertThat(result).isEmpty();
  }
}
