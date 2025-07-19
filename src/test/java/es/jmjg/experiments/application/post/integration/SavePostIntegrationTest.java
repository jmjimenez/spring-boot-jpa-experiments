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

import es.jmjg.experiments.application.post.SavePost;
import es.jmjg.experiments.application.post.exception.InvalidRequest;
import es.jmjg.experiments.application.user.exception.UserNotFound;
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
class SavePostIntegrationTest extends TestContainersConfig {

  @Autowired
  private SavePost savePost;

  @Autowired
  private PostRepository postRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private Environment environment;

  private User testUser;

  @BeforeEach
  void setUp() {
    // Clear the database before each test
    postRepository.deleteAll();
    userRepository.deleteAll();

    // Create a test user
    testUser = userRepository.save(UserFactory.createBasicUser());
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
  void save_ShouldSaveAndReturnPost() {
    // Given
    Post newPost = PostFactory.createPost(testUser, "New Post", "New Body");

    // When
    Post result = savePost.save(newPost);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getId()).isNotNull();
    assertThat(result.getTitle()).isEqualTo("New Post");
    assertThat(result.getBody()).isEqualTo("New Body");
    assertThat(result.getUser().getId()).isEqualTo(testUser.getId());

    // Verify it was actually saved to the database
    Optional<Post> savedPost = postRepository.findById(result.getId());
    assertThat(savedPost).isPresent();
    assertThat(savedPost.get().getTitle()).isEqualTo("New Post");
  }

  @Test
  void save_WhenPostHasNoUser_ShouldThrowInvalidRequest() {
    // Given
    Post newPost = PostFactory.createPostWithoutUser("New Post", "New Body");

    // When & Then
    assertThatThrownBy(() -> savePost.save(newPost))
        .isInstanceOf(InvalidRequest.class)
        .hasMessage("Post must have a user");

    // Verify no post was saved
    assertThat(postRepository.count()).isZero();
  }

  @Test
  void save_WhenUserIdProvidedAndUserExists_ShouldSetUserAndSave() {
    // Given
    Post newPost = PostFactory.createPostWithoutUser("New Post", "New Body");

    // When
    Post result = savePost.save(newPost, testUser.getUuid());

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getId()).isNotNull();
    assertThat(result.getTitle()).isEqualTo("New Post");
    assertThat(result.getBody()).isEqualTo("New Body");
    assertThat(result.getUser().getId()).isEqualTo(testUser.getId());

    // Verify it was actually saved to the database
    Optional<Post> savedPost = postRepository.findById(result.getId());
    assertThat(savedPost).isPresent();
    assertThat(savedPost.get().getUser().getId()).isEqualTo(testUser.getId());
  }

  @Test
  void save_WhenUserIdProvidedButUserNotFound_ShouldThrowUserNotFound() {
    // Given
    Post newPost = PostFactory.createPostWithoutUser("New Post", "New Body");
    java.util.UUID nonExistentUserUuid = java.util.UUID.randomUUID();

    // When & Then
    assertThatThrownBy(() -> savePost.save(newPost, nonExistentUserUuid))
        .isInstanceOf(UserNotFound.class)
        .hasMessage("User not found with uuid: " + nonExistentUserUuid);

    // Verify no post was saved
    assertThat(postRepository.count()).isZero();
  }

  @Test
  void save_WhenUserIdIsNull_ShouldThrowInvalidRequest() {
    // Given
    Post newPost = PostFactory.createPostWithoutUser("New Post", "New Body");

    // When & Then
    assertThatThrownBy(() -> savePost.save(newPost, null))
        .isInstanceOf(InvalidRequest.class)
        .hasMessage("Post must have a user");

    // Verify no post was saved
    assertThat(postRepository.count()).isZero();
  }

  @Test
  void save_WhenPostAlreadyHasUserAndUserIdProvided_ShouldKeepExistingUser() {
    // Given
    Post newPost = PostFactory.createPost(testUser, "New Post", "New Body");

    // When
    Post result = savePost.save(newPost, testUser.getUuid());

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getId()).isNotNull();
    assertThat(result.getTitle()).isEqualTo("New Post");
    assertThat(result.getBody()).isEqualTo("New Body");
    assertThat(result.getUser().getId()).isEqualTo(testUser.getId());

    // Verify it was actually saved to the database
    Optional<Post> savedPost = postRepository.findById(result.getId());
    assertThat(savedPost).isPresent();
    assertThat(savedPost.get().getUser().getId()).isEqualTo(testUser.getId());
  }
}
