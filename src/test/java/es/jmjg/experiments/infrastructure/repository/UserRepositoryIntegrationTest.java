package es.jmjg.experiments.infrastructure.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import es.jmjg.experiments.domain.entity.Post;
import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.shared.BaseJpaIntegration;
import es.jmjg.experiments.shared.PostFactory;
import es.jmjg.experiments.shared.TestDataSamples;
import es.jmjg.experiments.shared.UserFactory;

@Import({ UserRepositoryImpl.class, PostRepositoryImpl.class })
public class UserRepositoryIntegrationTest extends BaseJpaIntegration {

  @Autowired
  private UserRepositoryImpl userRepository;

  @Autowired
  private PostRepositoryImpl postRepository;

  @Test
  void shouldFindUserByEmail() {
    // When
    Optional<User> foundUser = userRepository.findByEmail(TestDataSamples.LEANNE_EMAIL);

    // Then
    assertThat(foundUser).isPresent();
    assertThat(foundUser.get().getEmail()).isEqualTo(TestDataSamples.LEANNE_EMAIL);
    assertThat(foundUser.get().getName()).isEqualTo(TestDataSamples.LEANNE_NAME);
    assertThat(foundUser.get().getUsername()).isEqualTo(TestDataSamples.LEANNE_USERNAME);
  }

  @Test
  void shouldNotFindUserByNonExistentEmail() {
    // When
    Optional<User> foundUser = userRepository.findByEmail("nonexistent@example.com");

    // Then
    assertThat(foundUser).isEmpty();
  }

  @Test
  void shouldFindUserByUsername() {
    // When
    Optional<User> foundUser = userRepository.findByUsername(TestDataSamples.ERVIN_USERNAME);

    // Then
    assertThat(foundUser).isPresent();
    assertThat(foundUser.get().getUsername()).isEqualTo(TestDataSamples.ERVIN_USERNAME);
    assertThat(foundUser.get().getEmail()).isEqualTo(TestDataSamples.ERVIN_EMAIL);
  }

  @Test
  void shouldNotFindUserByNonExistentUsername() {
    // When
    Optional<User> foundUser = userRepository.findByUsername("nonexistentuser");

    // Then
    assertThat(foundUser).isEmpty();
  }

  @Test
  void shouldFindUserByUuid() {
    // When
    Optional<User> foundUser = userRepository.findByUuid(TestDataSamples.CLEMENTINE_UUID);

    // Then
    assertThat(foundUser).isPresent();
    assertThat(foundUser.get().getUuid()).isEqualTo(TestDataSamples.CLEMENTINE_UUID);
    assertThat(foundUser.get().getName()).isEqualTo(TestDataSamples.CLEMENTINE_NAME);
  }

  @Test
  void shouldNotFindUserByNonExistentUuid() {
    // When
    UUID nonExistentUuid = UUID.randomUUID();
    Optional<User> foundUser = userRepository.findByUuid(nonExistentUuid);

    // Then
    assertThat(foundUser).isEmpty();
  }

  @Test
  void shouldDeleteUserByUuid() {
    // Given - using existing user from migration data
    User patricia = userRepository.findByUuid(TestDataSamples.PATRICIA_UUID).orElseThrow();
    assertThat(patricia).isNotNull();

    // Assert that Patricia has posts
    List<Post> patriciaPosts = postRepository.findByUserId(patricia.getId());
    assertThat(patriciaPosts).isNotEmpty();
    assertThat(patriciaPosts).hasSize(10);

    // When
    userRepository.deleteByUuid(TestDataSamples.PATRICIA_UUID);

    // Then
    assertThat(userRepository.findByUuid(TestDataSamples.PATRICIA_UUID)).isEmpty();

    // Assert that posts owned by Patricia have been deleted
    List<Post> remainingPatriciaPosts = postRepository.findByUserId(patricia.getId());
    assertThat(remainingPatriciaPosts).isEmpty();
  }

  @Test
  void shouldSaveAndRetrieveUser() {
    // Given
    User newUser = UserFactory.createUser("New User", "newuser@example.com", "newuser");
    UUID newUserUuid = newUser.getUuid();

    // When
    User savedUser = userRepository.save(newUser);

    // Then
    assertThat(savedUser.getId()).isNotNull();
    assertThat(savedUser.getUuid()).isEqualTo(newUserUuid);
    assertThat(savedUser.getName()).isEqualTo("New User");
    assertThat(savedUser.getEmail()).isEqualTo("newuser@example.com");
    assertThat(savedUser.getUsername()).isEqualTo("newuser");

    // Verify it can be retrieved
    Optional<User> retrievedUser = userRepository.findByUuid(newUserUuid);
    assertThat(retrievedUser).isPresent();
    assertThat(retrievedUser.get().getId()).isEqualTo(savedUser.getId());
  }

  @Test
  void shouldHandleMultipleUsers() {
    // Given - using existing users from migration data
    // When - verify all migration users exist
    // Then
    assertThat(userRepository.findByEmail(TestDataSamples.LEANNE_EMAIL)).isPresent();
    assertThat(userRepository.findByEmail(TestDataSamples.ERVIN_EMAIL)).isPresent();
    assertThat(userRepository.findByEmail(TestDataSamples.CLEMENTINE_EMAIL)).isPresent();
    assertThat(userRepository.findByEmail(TestDataSamples.PATRICIA_EMAIL)).isPresent();
    assertThat(userRepository.findByEmail(TestDataSamples.CHELSEY_EMAIL)).isPresent();

    assertThat(userRepository.findByUsername(TestDataSamples.LEANNE_USERNAME)).isPresent();
    assertThat(userRepository.findByUsername(TestDataSamples.ERVIN_USERNAME)).isPresent();
    assertThat(userRepository.findByUsername(TestDataSamples.CLEMENTINE_USERNAME)).isPresent();
    assertThat(userRepository.findByUsername(TestDataSamples.PATRICIA_USERNAME)).isPresent();
    assertThat(userRepository.findByUsername(TestDataSamples.CHELSEY_USERNAME)).isPresent();
  }

  @Test
  void shouldSaveUserWithPostsUsingCascade() {
    // Given
    User user = UserFactory.createUser("Cascade User", "cascade@example.com", "cascadeuser");

    Post post1 = PostFactory.createPost(user, "First Post", "This is the first post content");
    Post post2 = PostFactory.createPost(user, "Second Post", "This is the second post content");

    // Set up bidirectional relationship
    user.getPosts().add(post1);
    user.getPosts().add(post2);
    post1.setUser(user);
    post2.setUser(user);

    // When
    User savedUser = userRepository.save(user);

    // Then
    assertThat(savedUser.getId()).isNotNull();
    assertThat(savedUser.getPosts()).hasSize(2);

    // Verify posts were saved to database
    Optional<Post> foundPost1 = postRepository.findByTitle("First Post");
    Optional<Post> foundPost2 = postRepository.findByTitle("Second Post");

    assertThat(foundPost1).isPresent();
    assertThat(foundPost2).isPresent();

    assertThat(foundPost1.get().getUser().getId()).isEqualTo(savedUser.getId());
    assertThat(foundPost2.get().getUser().getId()).isEqualTo(savedUser.getId());

    assertThat(foundPost1.get().getTitle()).isEqualTo("First Post");
    assertThat(foundPost1.get().getBody()).isEqualTo("This is the first post content");
    assertThat(foundPost2.get().getTitle()).isEqualTo("Second Post");
    assertThat(foundPost2.get().getBody()).isEqualTo("This is the second post content");
  }
}