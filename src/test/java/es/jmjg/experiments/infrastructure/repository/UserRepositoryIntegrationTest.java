package es.jmjg.experiments.infrastructure.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import es.jmjg.experiments.domain.entity.Post;
import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.shared.BaseJpaIntegration;
import es.jmjg.experiments.shared.PostFactory;
import es.jmjg.experiments.shared.UserFactory;

public class UserRepositoryIntegrationTest extends BaseJpaIntegration {

  @Autowired
  UserRepository userRepository;

  @Autowired
  PostRepository postRepository;

  // Sample users from Flyway migration data
  private static final String LEANNE_NAME = "Leanne Graham";
  private static final String LEANNE_EMAIL = "leanne.graham@example.com";
  private static final String LEANNE_USERNAME = "leanne_graham";

  private static final String ERVIN_EMAIL = "ervin.howell@example.com";
  private static final String ERVIN_USERNAME = "ervin_howell";

  private static final UUID CLEMENTINE_UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440003");
  private static final String CLEMENTINE_NAME = "Clementine Bauch";
  private static final String CLEMENTINE_EMAIL = "clementine.bauch@example.com";
  private static final String CLEMENTINE_USERNAME = "clementine_bauch";

  private static final UUID PATRICIA_UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440004");
  private static final String PATRICIA_EMAIL = "patricia.lebsack@example.com";
  private static final String PATRICIA_USERNAME = "patricia_lebsack";

  private static final String CHELSEY_EMAIL = "chelsey.dietrich@example.com";
  private static final String CHELSEY_USERNAME = "chelsey_dietrich";

  @Test
  void shouldFindUserByEmail() {
    // When
    Optional<User> foundUser = userRepository.findByEmail(LEANNE_EMAIL);

    // Then
    assertThat(foundUser).isPresent();
    assertThat(foundUser.get().getEmail()).isEqualTo(LEANNE_EMAIL);
    assertThat(foundUser.get().getName()).isEqualTo(LEANNE_NAME);
    assertThat(foundUser.get().getUsername()).isEqualTo(LEANNE_USERNAME);
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
    Optional<User> foundUser = userRepository.findByUsername(ERVIN_USERNAME);

    // Then
    assertThat(foundUser).isPresent();
    assertThat(foundUser.get().getUsername()).isEqualTo(ERVIN_USERNAME);
    assertThat(foundUser.get().getEmail()).isEqualTo(ERVIN_EMAIL);
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
    Optional<User> foundUser = userRepository.findByUuid(CLEMENTINE_UUID);

    // Then
    assertThat(foundUser).isPresent();
    assertThat(foundUser.get().getUuid()).isEqualTo(CLEMENTINE_UUID);
    assertThat(foundUser.get().getName()).isEqualTo(CLEMENTINE_NAME);
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
    assertThat(userRepository.findByUuid(PATRICIA_UUID)).isPresent();

    // When
    userRepository.deleteByUuid(PATRICIA_UUID);

    // Then
    assertThat(userRepository.findByUuid(PATRICIA_UUID)).isEmpty();
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
    assertThat(userRepository.findByEmail(LEANNE_EMAIL)).isPresent();
    assertThat(userRepository.findByEmail(ERVIN_EMAIL)).isPresent();
    assertThat(userRepository.findByEmail(CLEMENTINE_EMAIL)).isPresent();
    assertThat(userRepository.findByEmail(PATRICIA_EMAIL)).isPresent();
    assertThat(userRepository.findByEmail(CHELSEY_EMAIL)).isPresent();

    assertThat(userRepository.findByUsername(LEANNE_USERNAME)).isPresent();
    assertThat(userRepository.findByUsername(ERVIN_USERNAME)).isPresent();
    assertThat(userRepository.findByUsername(CLEMENTINE_USERNAME)).isPresent();
    assertThat(userRepository.findByUsername(PATRICIA_USERNAME)).isPresent();
    assertThat(userRepository.findByUsername(CHELSEY_USERNAME)).isPresent();
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