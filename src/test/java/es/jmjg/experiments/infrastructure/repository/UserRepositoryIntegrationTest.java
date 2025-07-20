package es.jmjg.experiments.infrastructure.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
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

  private User testUser;
  private UUID testUserUuid;

  @BeforeEach
  void setUp() {
    testUser = UserFactory.createBasicUser();
    testUserUuid = testUser.getUuid();
    userRepository.save(testUser);
  }

  @Test
  void shouldFindUserByEmail() {
    // When
    Optional<User> foundUser = userRepository.findByEmail("test@example.com");

    // Then
    assertThat(foundUser).isPresent();
    assertThat(foundUser.get().getEmail()).isEqualTo("test@example.com");
    assertThat(foundUser.get().getName()).isEqualTo("Test User");
    assertThat(foundUser.get().getUsername()).isEqualTo("testuser");
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
    Optional<User> foundUser = userRepository.findByUsername("testuser");

    // Then
    assertThat(foundUser).isPresent();
    assertThat(foundUser.get().getUsername()).isEqualTo("testuser");
    assertThat(foundUser.get().getEmail()).isEqualTo("test@example.com");
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
    Optional<User> foundUser = userRepository.findByUuid(testUserUuid);

    // Then
    assertThat(foundUser).isPresent();
    assertThat(foundUser.get().getUuid()).isEqualTo(testUserUuid);
    assertThat(foundUser.get().getName()).isEqualTo("Test User");
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
    // Given
    User userToDelete = UserFactory.createUser("Delete Me", "delete@example.com", "deleteme");
    UUID userToDeleteUuid = userToDelete.getUuid();
    userRepository.save(userToDelete);

    // Verify user exists
    assertThat(userRepository.findByUuid(userToDeleteUuid)).isPresent();

    // When
    userRepository.deleteByUuid(userToDeleteUuid);

    // Then
    assertThat(userRepository.findByUuid(userToDeleteUuid)).isEmpty();
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
    // Given
    User user1 = UserFactory.createUser("User One", "user1@example.com", "user1");
    User user2 = UserFactory.createUser("User Two", "user2@example.com", "user2");
    User user3 = UserFactory.createUser("User Three", "user3@example.com", "user3");

    // When
    userRepository.save(user1);
    userRepository.save(user2);
    userRepository.save(user3);

    // Then
    assertThat(userRepository.findByEmail("user1@example.com")).isPresent();
    assertThat(userRepository.findByEmail("user2@example.com")).isPresent();
    assertThat(userRepository.findByEmail("user3@example.com")).isPresent();

    assertThat(userRepository.findByUsername("user1")).isPresent();
    assertThat(userRepository.findByUsername("user2")).isPresent();
    assertThat(userRepository.findByUsername("user3")).isPresent();
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